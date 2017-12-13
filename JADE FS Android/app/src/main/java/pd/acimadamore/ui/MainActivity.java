package pd.acimadamore.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.acimadamore.R;
import pd.acimadamore.events.FSEvent;
import pd.acimadamore.ui.services.JADERuntime;

public class MainActivity extends AppCompatActivity {

  private AgentEventsReceiver agentEventsReceiver;

  @BindView(R.id.main_container_ip)
  TextView mainContainerIP;
  @BindView(R.id.main_container_port)
  TextView mainContainerPort;
  @BindView(R.id.connection_progress_bar)
  ProgressBar progressBar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    agentEventsReceiver = new AgentEventsReceiver();
    IntentFilter intf   = new IntentFilter();
    intf.addAction(FSEvent.INITIALIZED.name());

    this.registerReceiver(agentEventsReceiver, intf);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    this.unregisterReceiver(agentEventsReceiver);
  }

  public void connect(View view) {
    if(validateInput()){
      progressBar.setVisibility(View.VISIBLE);

      if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        this.initJADE();
      }
      else{
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
      }
    }
  }

  private void initJADE(){
    String agentName = Build.MODEL.replaceAll("\\s+","") + "-" + String.valueOf((new Random()).nextInt(999));

    JADERuntime.instance.init(mainContainerIP.getText().toString(), mainContainerPort.getText().toString(), agentName);
  }

  private class AgentEventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equalsIgnoreCase(FSEvent.INITIALIZED.name())) {

        progressBar.setVisibility(View.GONE);

        startActivity(new Intent(MainActivity.this, FileSharingActivity.class));
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case 100:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
          initJADE();
        break;
    }
  }

  private Boolean validateInput(){
    Boolean valid = true;

    if(mainContainerIP.getText().toString().trim().length() == 0){
      mainContainerIP.setError(this.getResources().getString(R.string.blank_validation_error));
      valid = false;
    }

    if(mainContainerPort.getText().toString().trim().length() == 0){
      mainContainerPort.setError(this.getResources().getString(R.string.blank_validation_error));
      valid = false;
    }

    return valid;
  }

}
