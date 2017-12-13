package pd.acimadamore.events;

public abstract class FSNotifier {

  public static FSNotifier instance; 
  
  public abstract void notify(FSEvent event);
  
}
