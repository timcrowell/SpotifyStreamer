package com.timcrowell.android.udacityproject1.spotifystreamer.app.List;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Searches Spotify for Artists and populates a SpotifyListAdapter with the results.
 */
public class ArtistSearch extends SpotifySearch {
    private static final String TAG = ArtistSearch.class.getSimpleName();

    public ArtistSearch(SpotifyListAdapter adapter) {
        super(adapter);
    }

    // This method runs on a background thread.
    @Override
    List queryToPerform(String query) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        // Get CountryCode from Shared Prefs.  If it fails, default to US.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Streamer.getInstance().service.getBaseContext());
        String country = sharedPreferences.getString("countryCode", "US");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("country", country);

        ArtistsPager results = spotify.searchArtists(query, options);

        return results.artists.items;

    }
}
