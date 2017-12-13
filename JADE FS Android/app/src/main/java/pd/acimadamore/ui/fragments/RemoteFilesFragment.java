package pd.acimadamore.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jade.core.AID;
import pd.acimadamore.FileSharerApp;
import pd.acimadamore.R;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ui.adapters.SharedFileAdapter;
import pd.acimadamore.ui.presenters.SharedFilePresenter;
import pd.acimadamore.ui.services.JADERuntime;

public class RemoteFilesFragment extends Fragment implements FilesFragment{
  @BindView(R.id.files)
  ListView files;

  public RemoteFilesFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.file_list_fragment, container, false);

    ButterKnife.bind(this, view);

    updateFileList();

    return view;
  }

  public void updateFileList() {
    ArrayList<SharedFilePresenter> sfpl = new ArrayList<SharedFilePresenter>();

    for (Map.Entry<AID, SharedDirectory> entry : JADERuntime.instance.getAgent().getRemoteDirectories().entrySet()) {
      sfpl.addAll(SharedFilePresenter.DirectoryToSharedFilePresenters(entry.getValue(), false));
    }


    files.setAdapter(new SharedFileAdapter(this.getActivity(), sfpl));
  }


}
