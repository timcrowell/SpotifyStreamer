package com.timcrowell.android.udacityproject1.spotifystreamer.app.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Searches Spotify for an Artist by ID and populates a SpotifyListAdapter with the artist's top tracks.
 */
public class TopTrackSearch extends SpotifySearch {
    private static final String TAG = TopTrackSearch.class.getSimpleName();

    public TopTrackSearch(SpotifyListAdapter adapter) {
        super(adapter);
    }

    // This method runs on a background thread.
    @Override
    List queryToPerform(String query) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Log.d(TAG, "Searching for Artist ID: " + query);


        // Get CountryCode from Shared Prefs.  If it fails, default to US.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Streamer.getInstance().service.getBaseContext());
        String country = sharedPreferences.getString("countryCode", "US");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("country", country);

        Tracks results = spotify.getArtistTopTrack(query, options);

        return results.tracks;

    }
}
