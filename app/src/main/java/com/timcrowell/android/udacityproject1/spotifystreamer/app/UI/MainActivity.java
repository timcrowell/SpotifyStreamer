package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observable;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observer;

/**
 * This is a single activity app.  The various screens/layouts will be changed
 * via Fragments.
 */
public class MainActivity extends AppCompatActivity implements Observer {
    private static final String TAG = MainActivity.class.getSimpleName();

    public boolean isTabletLayout;

    private Toolbar toolbar;
    private Menu toolbarMenu;
    private Streamer streamer;
    private Observable streamerObservable;

    @Override
    public void setSubject(Observable subject) {
        this.streamerObservable = subject;
    }

    @Override
    public void update() {

        if (toolbarMenu != null) {
            MenuItem nowPlaying = toolbarMenu.findItem(R.id.nowPlaying);

            if ( isTabletLayout
                    || getSupportFragmentManager().findFragmentByTag("PLAYER_FRAGMENT") == null
                    || ! getSupportFragmentManager().findFragmentByTag("PLAYER_FRAGMENT").isVisible() )
            {
                nowPlaying.setVisible(streamer.service.isPlaying());
            } else {
                nowPlaying.setVisible(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar replaces the ActionBar
        toolbar = (Toolbar) findViewById(R.id.spotifyToolbar);
        setSupportActionBar(toolbar);


        // If we're using a tablet layout
        if (findViewById(R.id.fragment_search_container) != null) {

            isTabletLayout = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_search_container, new SearchFragment(), "SEARCH_FRAGMENT")
                        .commit();
            }

            // If this is a phone layout
        } else {

            isTabletLayout = false;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new SearchFragment(), "SEARCH_FRAGMENT")
                        .commit();
            }
        }


        // Make sure to continue showing Now Playing button if activity has been restarted.
        if (streamer != null) { streamer.monitor.notifyObservers(); }
    }

    public void showUpButton(boolean shouldShow) {
        try {

            if (shouldShow) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "NPE when getting actionbar.)");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        toolbarMenu = menu;

        // Double-check Now-Playing after rotation
        streamer.monitor.forceNotifyObservers();
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        streamer = Streamer.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        streamer.monitor.register(this);
    }

    // TODO - Add things to options menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.nowPlaying:

                hideKeyboard();

                FragmentManager fragmentManager = this.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                // For phone layouts
                if (!isTabletLayout) {

                    // Create a Fragment to contain results and pass it the query to search for.
                    PlayerFragment playerFragment = new PlayerFragment();

                    // Swap this Fragment out with the new one.
                    transaction.replace(R.id.container, playerFragment, "PLAYER_FRAGMENT");
                    transaction.addToBackStack(null);
                    transaction.commit();

                    // For Tablet Layouts
                } else {

                    DialogFragment playerFragment = new PlayerFragment();
                    playerFragment.show(transaction, "PLAYER_FRAGMENT");
                }
                return true;

            // Support Up button behavior.
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    Log.d("MainActivity", "Up pressed. Popping fragment backstack.");
                    fm.popBackStack();
                } else {
                    Log.d("MainActivity", "Up pressed. Nothing on fragment backstack, going back instead.");
                    super.onBackPressed();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
