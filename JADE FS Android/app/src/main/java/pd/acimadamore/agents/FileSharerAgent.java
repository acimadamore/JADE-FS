package pd.acimadamore.agents;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.domain.FIPANames;
import jade.domain.introspection.IntrospectionOntology;
import jade.domain.mobility.MobilityOntology;
import jade.content.lang.sl.SLCodec;

import java.util.HashMap;
import java.io.File;

import jade.core.AID;

import pd.acimadamore.behaviours.AgentLifeCycleEventsHandler;
import pd.acimadamore.behaviours.DirectoryInformationReceiver;
import pd.acimadamore.behaviours.DirectoryInformationSender;
import pd.acimadamore.behaviours.FileReader;
import pd.acimadamore.behaviours.FileWriter;
import pd.acimadamore.events.FSEvent;
import pd.acimadamore.events.FSNotifier;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedFile;
import pd.acimadamore.ontologies.SharedDirectoryOntology;

public class FileSharerAgent extends Agent implements FileSharer {

  private static final long serialVersionUID = 1L;

  private Location                      originalLocation;

  private HashMap<AID, SharedDirectory> remoteDirectories = new HashMap<AID, SharedDirectory>();

  private String                        mySharedDirectoryPath;

  // Attributes needed to download remote file
  private Boolean                 remoteFileFound;
  private byte[]                  remoteFileData;
  private String                  remoteFileDirectory;
  private String                  remoteFilename;

  @Override
  protected void setup() {

    this.registerLanguagesAndOntologies();

    // The first and only argument is the path of the shared directory by this agent
    Object[] args = getArguments();
    if (args != null && args.length > 0) {
      if (args[0] instanceof File) {
        this.mySharedDirectoryPath = ((File) args[0]).getPath();
      }
    }

    this.originalLocation = this.here();

    addBehaviour(new DirectoryInformationSender(this.getSharedDirectory()));
    addBehaviour(new DirectoryInformationReceiver());
    addBehaviour(new AgentLifeCycleEventsHandler());

    this.registerO2AInterface(FileSharer.class, this);

    this.notify(FSEvent.INITIALIZED);
  }

  @Override
  protected void afterMove() {
    super.afterMove();

    this.registerLanguagesAndOntologies();

    if(this.isAtRemoteLocation()){
      addBehaviour(new FileReader(new File(this.remoteFileDirectory), this.remoteFilename));
    }
    else{
      if(this.remoteFileFound){
        File pathToWrite = new File(this.mySharedDirectoryPath, this.remoteFilename);

        addBehaviour(new FileWriter(pathToWrite, this.remoteFileData));
      }
      else{
        this.notify(FSEvent.FILE_NOT_FOUND);
      }

    }
  }

  public void registerLanguagesAndOntologies(){
    this.getContentManager().registerLanguage(new SLCodec());
    this.getContentManager().registerOntology(MobilityOntology.getInstance());
    this.getContentManager().registerOntology(IntrospectionOntology.getInstance());
    this.getContentManager().registerOntology(SharedDirectoryOntology.getInstance());

    this.getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
  }

  public SharedDirectory getSharedDirectory(){
    return SharedDirectory.createFromPathAndLocation(new File(this.mySharedDirectoryPath), this.here());
  }

  public void removeRemoteDirectory(AID aid){
    this.remoteDirectories.remove(aid);

    this.notify(FSEvent.REMOTE_DIRECTORY_REMOVED);
  }

  public void addRemoteDirectory(AID aid, SharedDirectory directory){
    this.remoteDirectories.put(aid, directory);

    this.notify(FSEvent.REMOTE_DIRECTORY_ADDED);
  }

  public HashMap<AID, SharedDirectory> getRemoteDirectories(){
    return this.remoteDirectories;
  }

  public void setRemoteFileData(byte[] remoteFileData){
    this.remoteFileData = remoteFileData;
  }

  public boolean isAtRemoteLocation(){
    return !this.here().equals(this.originalLocation);
  }

  public void returnToOriginalLocation(){
    this.doMove(this.originalLocation);
  }

  public void notify(FSEvent event){
    FSNotifier.instance.notify(event);
  }

  public void remoteFileNotFound(){
    this.remoteFileFound = false;
  }

  public void remoteFileFound(){
    this.remoteFileFound = true;
  }

  public void downloadRemoteFile(SharedDirectory directory, SharedFile file){
    this.remoteFileDirectory = directory.getPath();
    this.remoteFilename      = file.getName();

    ContainerID cid = new ContainerID();
    cid.setName(directory.getLocation());

    this.doMove(cid);
  }
}
