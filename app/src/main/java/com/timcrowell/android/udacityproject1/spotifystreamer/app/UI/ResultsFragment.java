package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.SpotifyListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.SpotifyListAdapter;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.SpotifySearch;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.TopTrackSearch;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.RetainedFragment;

import java.util.List;

/**
 * This Fragment is used to display a list of items returned from a search.  setQuery(String)
 * needs to be called before it will display anything.
 */
public class ResultsFragment  extends Fragment {
    private static final String TAG = ResultsFragment.class.getSimpleName();

    private FragmentActivity myContext;

    private SpotifyListAdapter spotifyListAdapter;
    private SpotifySearch spotifySearch;
    private String query;

    public ResultsFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        if (Streamer.getInstance() != null) {
            Streamer.getInstance().monitor.forceNotifyObservers();
        }
        super.onResume();
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
        spotifySearch = new TopTrackSearch(spotifyListAdapter);

        //Attach the ListAdapter to the List View
        final ListView listView = (ListView) rootView.findViewById(R.id.listview_searchresults);
        listView.setAdapter(spotifyListAdapter);

        // Are we a phone or a tablet?
        final boolean isTwoPane;
        if (getActivity().findViewById(R.id.fragment_results_container) == null) {
            isTwoPane = false;
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showUpButton(true);
        } else {
            isTwoPane = true;
        }

        // Handle clicks on list Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyListItem listItem = (SpotifyListItem) view.getTag();

                if (listItem.getType() == SpotifyListItem.Type.TRACK) {

                    // Get playlist
                    List<TrackListItem> playlist = spotifyListAdapter.getPlaylistItems();

                    if (playlist != null) {

                        // Setup Streamer and start playback
                        Streamer streamer = Streamer.getInstance();
                        int playlistIndex = ((TrackListItem) listItem).getPlaylistIndex();
                        streamer.controller.setPlaylist(playlist);
                        streamer.controller.setListItem(playlistIndex);
                        streamer.controller.start();

                    } else {
                        Log.d(TAG, "Playlist is null");
                    }

                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                    // For phone layouts
                    if (!isTwoPane) {

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
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("PLAYER_FRAGMENT");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);



            }
        });

        // Get the results and display them.
        if (query != null) {
            spotifySearch.search(query);
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