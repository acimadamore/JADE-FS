package pd.acimadamore.events;

import android.content.Intent;

import pd.acimadamore.FileSharerApp;

public class AndroidNotifier extends FSNotifier {

  @Override
  public void notify(FSEvent event) {
    Intent broadcast = new Intent();
    broadcast.setAction(event.toString());

    FileSharerApp.getAppContext().sendBroadcast(broadcast);
  }

}
