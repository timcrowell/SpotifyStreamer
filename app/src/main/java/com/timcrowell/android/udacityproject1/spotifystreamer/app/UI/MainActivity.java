package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;

/**
 * This is a single activity app.  The various screens/layouts will be changed
 * via Fragments.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public boolean isTabletLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            // Toolbar replaces the ActionBar
            Toolbar mToolbar = (Toolbar) findViewById(R.id.spotifyToolbar);
            setSupportActionBar(mToolbar);

            // If we're using a tablet layout
            if (findViewById(R.id.fragment_search_container) != null) {

                isTabletLayout = true;

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_search_container, new SearchFragment())
                        .commit();
                // If this is a phone layout
            } else {

                isTabletLayout = false;

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new SearchFragment())
                        .commit();
            }
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
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "We're going to create a Streamer now.");
        Streamer.getInstance(getApplicationContext());
        Log.d(TAG,  "We created that Streamer.");
    }

    // TODO - Add things to options menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_settings:
                return true;

            // Support Up button behavior.
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
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

}
