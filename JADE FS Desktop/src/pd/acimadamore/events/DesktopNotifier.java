package pd.acimadamore.events;

public class DesktopNotifier extends FSNotifier {

  private static Boolean agentInitialized = false;

  @Override
  public void notify(FSEvent event) {
    switch(event){
      case INITIALIZED:
        agentInitialized = true;
        break;
      case REMOTE_DIRECTORY_ADDED:
        break;
      case REMOTE_DIRECTORY_REMOVED:
        break;
      case FILE_DOWNLOADED:
        break;
    }
  }

  public static Boolean isAgentInitialized(){
    return agentInitialized;
  }

}
