package pd.acimadamore;

import android.app.Application;
import android.content.Context;

import pd.acimadamore.agents.FileSharer;
import pd.acimadamore.events.AndroidNotifier;
import pd.acimadamore.events.FSNotifier;
import pd.acimadamore.ui.services.JADERuntime;

public class FileSharerApp extends Application {

  private static Context context;


  @Override
  public void onCreate() {
    super.onCreate();

    FileSharerApp.context = getApplicationContext();

    FSNotifier.instance   = new AndroidNotifier();
    JADERuntime.instance  = new JADERuntime(this.context);
  }

  public static Context getAppContext() {
    return FileSharerApp.context;
  }
}
