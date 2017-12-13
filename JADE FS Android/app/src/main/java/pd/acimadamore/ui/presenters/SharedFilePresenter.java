package pd.acimadamore.ui.presenters;

import java.util.ArrayList;

import pd.acimadamore.FileSharerApp;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedFile;

public class SharedFilePresenter {

  private Boolean         isLocal;
  private SharedDirectory directory;
  private SharedFile      file;

  public SharedFilePresenter(SharedDirectory directory, SharedFile file, Boolean isLocal) {
    this.isLocal   = isLocal;
    this.directory = directory;
    this.file      = file;
  }

  public SharedDirectory getDirectory() {
    return this.directory;
  }

  public SharedFile getFile() {
    return this.file;
  }

  public Boolean isLocal() {
    return this.isLocal;
  }

  public String getLocation() {
    return this.directory.getLocation();
  }

  public String getFilename() {
    return this.file.getName();
  }


  public static ArrayList<SharedFilePresenter> DirectoryToSharedFilePresenters(SharedDirectory directory, Boolean isLocal){
    ArrayList<SharedFilePresenter> sfpl = new ArrayList<SharedFilePresenter>();

    for (SharedFile sf : directory.getFiles()) {
      sfpl.add(new SharedFilePresenter(directory, sf, isLocal));
    }

    return sfpl;
  }

}
