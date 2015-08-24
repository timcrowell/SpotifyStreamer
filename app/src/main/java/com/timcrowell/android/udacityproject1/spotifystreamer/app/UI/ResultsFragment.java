package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.SpotifyListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers.SpotifyListAdapter;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers.SpotifySearcher;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers.TopTracksSearcher;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Utils.RetainedFragment;

import java.util.List;

/**
 * This Fragment is used to display a list of items returned from a search.  setQuery(String)
 * needs to be called before it will display anything.
 */
public class ResultsFragment  extends Fragment {
    private static final String TAG = ResultsFragment.class.getSimpleName();

    private FragmentActivity myContext;

    private SpotifyListAdapter spotifyListAdapter;
    private SpotifySearcher spotifySearcher;
    private String query;

    public ResultsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    // Query gets passed in here from the calling fragment before displaying the fragment.
    public void setQuery(String query) {
        this.query = query;

        // This is a new query, so remove any stale search results from the RetainedFragment.
        RetainedFragment retainedFragment = RetainedFragment.getInstance();
        if (retainedFragment.containsKey("ResultsFragment_ListAdapter")) {
            retainedFragment.remove("ResultsFragment_ListAdapter");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);

        RetainedFragment retainedFragment = RetainedFragment.getInstance();
        if (retainedFragment.containsKey("ResultsFragment_ListAdapter")) {

            // If we're restoring after a device rotation, get the ListAdapter from the Results Fragment.
            spotifyListAdapter = (SpotifyListAdapter) retainedFragment.remove("ResultsFragment_ListAdapter");

        } else {

            // Otherwise, create a new ListAdapter to display new results in.
            spotifyListAdapter = new SpotifyListAdapter(getActivity());
        }

        // Attach the ListAdapter to the Searcher
        // TODO - Make this smarter so it will get more than just top tracks.
        spotifySearcher = new TopTracksSearcher(spotifyListAdapter);

        //Attach the ListAdapter to the List View
        final ListView listView = (ListView) rootView.findViewById(R.id.listview_searchresults);
        listView.setAdapter(spotifyListAdapter);



        // Handle clicks on list Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyListItem listItem = (SpotifyListItem) view.getTag();

                if (listItem.getType() == SpotifyListItem.Type.TRACK) {

                    // Build playlist
                    final List<TrackListItem> playlist = spotifyListAdapter.getPlaylistItems();
                    Log.d(TAG, "Built Playlist");
                    if (playlist == null) {
                        Log.d(TAG, "Playlist is null");
                    }else {
                        Log.d(TAG, "Playlist is not null.");
                    }

                    TrackListItem trackListItem = (TrackListItem) listItem;

                    Streamer streamer = Streamer.getInstance();
                    streamer.controller.setPlayList(playlist);
                    streamer.controller.setListItem(trackListItem.getPlaylistIndex());
                    streamer.controller.start();

                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                    // Create a Fragment to contain results and pass it the query to search for.
                    PlayerFragment playerFragment = new PlayerFragment();

                    // Swap this Fragment out with the new one.
                    transaction.replace(R.id.container, playerFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        });

        // Get the results and display them.
        if (query != null) {
            spotifySearcher.search(query);
        }

        return rootView;
    }

    // Stash the ListAdapter into the RetainedFragment so we don't lose our results across
    // configuration changes.
    @Override
    public void onStop() {
        super.onStop();
        if (spotifyListAdapter != null) {
            RetainedFragment retainedFragment = RetainedFragment.getInstance();
            retainedFragment.put("ResultsFragment_ListAdapter", spotifyListAdapter);
        }
    }
}