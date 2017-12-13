package pd.acimadamore.agents;

import java.util.HashMap;

import jade.core.AID;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedFile;

public interface FileSharer {

  public void downloadRemoteFile(SharedDirectory directory, SharedFile file);
  
  public SharedDirectory getSharedDirectory();
  
  public HashMap<AID, SharedDirectory> getRemoteDirectories();
  
}
