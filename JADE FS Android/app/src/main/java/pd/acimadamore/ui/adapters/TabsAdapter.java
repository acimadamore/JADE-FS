package pd.acimadamore.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {
  private final List<Fragment> tabsList       = new ArrayList<>();
  private final List<String>   tabsTitlesList = new ArrayList<>();

  public TabsAdapter(FragmentManager manager) {
    super(manager);
  }

  @Override
  public Fragment getItem(int position) {
    return tabsList.get(position);
  }

  @Override
  public int getCount() {
    return tabsList.size();
  }

  public void add(Fragment fragment, String title) {
    tabsList.add(fragment);
    tabsTitlesList.add(title);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return tabsTitlesList.get(position);
  }
}
