package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observable;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observer;
import kaaes.spotify.webapi.android.models.TrackSimple;

import java.util.Map;

/**
 * This is a single activity app.  The various screens/layouts will be changed
 * via Fragments.
 */
public class MainActivity extends AppCompatActivity implements Observer {
    private static final String TAG = MainActivity.class.getSimpleName();

    public boolean isTabletLayout;

    // Used when the user clicks on the notification to come back to the PlayerFragment.
    public static final String ACTION_DISPLAYPLAYER = "Display Player";

    private Toolbar toolbar;
    private Menu toolbarMenu;
    private Streamer streamer;
    private Observable streamerObservable;
    private ShareActionProvider shareActionProvider;

    @Override
    public void setSubject(Observable subject) {
        this.streamerObservable = subject;
    }

    // Handle displaying the NowPlaying button
    @Override
    public void update() {
        if (toolbarMenu != null) {

            MenuItem nowPlaying = toolbarMenu.findItem(R.id.nowPlaying);
            if ( isTabletLayout
                    || getSupportFragmentManager().findFragmentByTag("PLAYER_FRAGMENT") == null
                    || ! getSupportFragmentManager().findFragmentByTag("PLAYER_FRAGMENT").isVisible() )
            {
                nowPlaying.setVisible(streamer.controller.isPlaying());
            } else {
                nowPlaying.setVisible(false);
            }

            MenuItem shareButton = toolbarMenu.findItem((R.id.shareButton));
            if (streamer.controller.isSongLoaded()) {
                shareButton.setVisible(true);
                shareActionProvider.setShareIntent(createShareIntent());
            } else {
                shareButton.setVisible(false);
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

        // Open up player if we're coming from the notification.
        if (getIntent().getAction() == ACTION_DISPLAYPLAYER) {
            displayPlayerFragment();
        }
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

        // Locate MenuItem with ShareActionProvider
        MenuItem shareButton = menu.findItem(R.id.shareButton);

        // Fetch and store ShareActionProvider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareButton);

        // Double-check Now-Playing after rotation
        streamer.monitor.notifyObservers();
        return true;
    }

    private Intent createShareIntent() {

        Map<String, String> trackUrls = ((TrackSimple) streamer.controller.getCurrentTrack().getModel()).external_urls;

        if (trackUrls != null) {

            String url = trackUrls.entrySet().iterator().next().getValue();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Track from Spotify Streamer.");
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            return shareIntent;

        } else {

            Log.d(TAG, "No External URL to share for current track.");
            return null;
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.nowPlaying:
                displayPlayerFragment();
                return true;

            // Support Up button behavior.
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    // Up pressed, but there's nothing on the fragment backstack, going back instead
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

    public void displayPlayerFragment() {

        hideKeyboard();

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        // For phone layouts
        if (!isTabletLayout) {

            PlayerFragment playerFragment = new PlayerFragment();

            // Swap old fragment out with the new one.
            transaction.replace(R.id.container, playerFragment, "PLAYER_FRAGMENT");
            transaction.addToBackStack(null);
            transaction.commit();

            // For Tablet Layouts
        } else {

            // Display fragment on top of existing UI.
            DialogFragment playerFragment = new PlayerFragment();
            playerFragment.show(transaction, "PLAYER_FRAGMENT");
        }
    }
}
