package pd.acimadamore.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pd.acimadamore.FileSharerApp;
import pd.acimadamore.R;
import pd.acimadamore.ui.adapters.SharedFileAdapter;
import pd.acimadamore.ui.presenters.SharedFilePresenter;
import pd.acimadamore.ui.services.JADERuntime;

public class SharedFilesFragment extends Fragment implements FilesFragment {

  @BindView(R.id.files)
  ListView files;

  public SharedFilesFragment() {
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

    this.updateFileList();

    return view;
  }

  public void updateFileList() {
    SharedFileAdapter sfa = new SharedFileAdapter(this.getActivity(), SharedFilePresenter.DirectoryToSharedFilePresenters(JADERuntime.instance.getAgent().getSharedDirectory(), true));

    files.setAdapter(sfa);
  }

}
