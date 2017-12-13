package pd.acimadamore.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.acimadamore.FileSharerApp;
import pd.acimadamore.R;
import pd.acimadamore.events.FSEvent;
import pd.acimadamore.ui.adapters.TabsAdapter;
import pd.acimadamore.ui.fragments.FilesFragment;
import pd.acimadamore.ui.fragments.RemoteFilesFragment;
import pd.acimadamore.ui.fragments.SharedFilesFragment;
import pd.acimadamore.ui.services.JADERuntime;

import static android.widget.Toast.LENGTH_SHORT;

public class FileSharingActivity extends AppCompatActivity {

  private AgentEventsReceiver agentEventsReceiver;

  private static int SHARED_FILES_TAB_INDEX = 0;
  private static int REMOTE_FILES_TAB_INDEX = 1;

  @BindView(R.id.viewpager)
  ViewPager viewpager;
  @BindView(R.id.tabs)
  TabLayout tabs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_sharing);
    ButterKnife.bind(this);

    agentEventsReceiver = new AgentEventsReceiver();

    IntentFilter intf = new IntentFilter();
    intf.addAction(FSEvent.REMOTE_DIRECTORY_ADDED.name());
    intf.addAction(FSEvent.REMOTE_DIRECTORY_REMOVED.name());
    intf.addAction(FSEvent.FILE_NOT_FOUND.name());
    intf.addAction(FSEvent.FILE_DOWNLOADED.name());

    this.registerReceiver(agentEventsReceiver, intf);

    TabsAdapter fpa = new TabsAdapter(getSupportFragmentManager());
    fpa.add(new SharedFilesFragment(), getResources().getString(R.string.shared_files_tab_title));
    fpa.add(new RemoteFilesFragment(), getResources().getString(R.string.remote_files_tab_title));
    viewpager.setAdapter(fpa);

    tabs.setupWithViewPager(viewpager);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    this.unregisterReceiver(agentEventsReceiver);

    JADERuntime.instance.shutdownContainer();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();

    JADERuntime.instance.shutdownContainer();
  }

  private class AgentEventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      TabsAdapter   vpa       = (TabsAdapter) viewpager.getAdapter();
      FilesFragment fragment  = null;
      String        toastMsg  = null;

      if (intent.getAction().equalsIgnoreCase(FSEvent.REMOTE_DIRECTORY_ADDED.name())) {

        fragment = (FilesFragment) vpa.getItem(FileSharingActivity.REMOTE_FILES_TAB_INDEX);

        toastMsg = FileSharingActivity.this.getResources().getString(R.string.remote_directory_added_toast);

        fragment.updateFileList();
      }

      if (intent.getAction().equalsIgnoreCase(FSEvent.REMOTE_DIRECTORY_REMOVED.name())) {

        fragment = (FilesFragment) vpa.getItem(FileSharingActivity.REMOTE_FILES_TAB_INDEX);

        toastMsg = FileSharingActivity.this.getResources().getString(R.string.remote_directory_removed_toast);

        fragment.updateFileList();
      }

      if (intent.getAction().equalsIgnoreCase(FSEvent.FILE_DOWNLOADED.name())) {

        fragment = (FilesFragment) vpa.getItem(FileSharingActivity.SHARED_FILES_TAB_INDEX);

        toastMsg = FileSharingActivity.this.getResources().getString(R.string.file_downloaded_toast);

        fragment.updateFileList();
      }

      if (intent.getAction().equalsIgnoreCase(FSEvent.FILE_NOT_FOUND.name())) {

        toastMsg = FileSharingActivity.this.getResources().getString(R.string.file_not_found_toast);
      }

      Toast.makeText(FileSharerApp.getAppContext(), toastMsg, LENGTH_SHORT).show();
    }
  }

}
