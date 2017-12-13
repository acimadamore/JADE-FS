package pd.acimadamore.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.acimadamore.FileSharerApp;
import pd.acimadamore.R;
import pd.acimadamore.ui.presenters.SharedFilePresenter;
import pd.acimadamore.ui.services.JADERuntime;

import static android.widget.Toast.LENGTH_SHORT;
import static pd.acimadamore.R.string.downloading_file_toast;


public class SharedFileAdapter extends ArrayAdapter<SharedFilePresenter> implements View.OnClickListener {

  public SharedFileAdapter(Context context, ArrayList<SharedFilePresenter> dataSet) {
    super(context, R.layout.file, dataSet);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    SharedFilePresenter sharedFile = this.getItem(position);
    ViewHolder          holder;
    LayoutInflater      inflater   = LayoutInflater.from(getContext());

    if (convertView != null) {
      holder = (ViewHolder) convertView.getTag();
    }
    else {
      convertView = inflater.inflate(R.layout.file, parent, false);
      holder = new ViewHolder(convertView);

      convertView.setTag(holder);
    }

    holder.filename.setText(sharedFile.getFilename());

    if(!sharedFile.isLocal()){
      holder.location.setText(sharedFile.getLocation());

      holder.downloadFileButton.setTag(position);
      holder.downloadFileButton.setOnClickListener(this);
      holder.downloadFileButton.setVisibility(View.VISIBLE);
    }

    return convertView;
  }

  @Override
  public void onClick(View v) {
    Integer position = (Integer)v.getTag();

    SharedFilePresenter sfp = (SharedFilePresenter) getItem(position);

    Toast.makeText(FileSharerApp.getAppContext(), FileSharerApp.getAppContext().getResources().getString(downloading_file_toast), LENGTH_SHORT).show();

    JADERuntime.instance.getAgent().downloadRemoteFile(sfp.getDirectory(), sfp.getFile());
  }

  public static class ViewHolder {
    @BindView(R.id.filename)
    TextView filename;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.download_file_btn)
    ImageButton downloadFileButton;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }
}
