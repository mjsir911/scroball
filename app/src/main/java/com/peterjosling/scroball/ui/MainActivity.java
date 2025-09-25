package com.peterjosling.scroball.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.common.collect.ImmutableList;
import com.peterjosling.scroball.R;
import com.peterjosling.scroball.ScroballApplication;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_INITIAL_TAB = "initial_tab";
  public static final int TAB_NOW_PLAYING = 0;
  public static final int TAB_SCROBBLE_HISTORY = 1;

  private ScroballApplication application;

  /**
   * The {@link PagerAdapter} that will provide fragments for each of the
   * sections. We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded
   * fragment in memory. If this becomes too memory intensive, it may be best to switch to a {@link
   * FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;

  /** The {@link ViewPager} that will host the section contents. */
  private ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    application = (ScroballApplication) getApplication();
    application.startListenerService();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    TabLayout tabLayout = findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);

    // Initial tab may have been specified in the intent.
    int initialTab = getIntent().getIntExtra(EXTRA_INITIAL_TAB, TAB_NOW_PLAYING);
    mViewPager.setCurrentItem(initialTab);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.settings_item) {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivityForResult(intent, 1);
        return true;
    } else if (itemId == R.id.privacy_policy_item) {
        Intent browserIntent =
            new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://scroball.peterjosling.com/privacy_policy.html"));
        startActivity(browserIntent);
        return true;
    } else if (itemId == R.id.logout_item) {
        logout();
        return true;
    } else {
        return super.onOptionsItemSelected(item);
    }

  }

  public void logout() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.are_you_sure)
        .setMessage(R.string.logout_confirm)
        .setPositiveButton(
            android.R.string.yes,
            (dialog, whichButton) -> {
              application.logout();

              Intent intent = new Intent(this, SplashScreen.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
              finish();
            })
        .setNegativeButton(android.R.string.no, null)
        .show();
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
   * sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments =
        ImmutableList.of(new NowPlayingFragment(), new ScrobbleHistoryFragment());

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return getString(R.string.tab_now_playing);
        case 1:
          return getString(R.string.tab_history);
      }
      return null;
    }
  }

  private void purchaseFailed() {
    new AlertDialog.Builder(this)
        .setMessage(R.string.purchase_failed)
        .setPositiveButton(android.R.string.ok, null)
        .create()
        .show();
  }
}
