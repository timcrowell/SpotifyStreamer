package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers;

import android.util.Log;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

import java.util.HashMap;
import java.util.List;

/**
 * Searches Spotify for an Artist by ID and populates a SpotifyListAdapter with the artist's top tracks.
 */
public class TopTracksSearcher extends SpotifySearcher {
    private static final String TAG = TopTracksSearcher.class.getSimpleName();

    public TopTracksSearcher(SpotifyListAdapter adapter) {
        super(adapter);
    }

    // This method runs on a background thread.
    @Override
    List queryToPerform(String query) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Log.d(TAG, "Searching for Artist ID: " + query);

        // Without the "country, US" bit, Spotify returns a 400 error.
        HashMap<String, Object> countryMap = new HashMap<String, Object>();
        countryMap.put("country", "US");
        Tracks results = spotify.getArtistTopTrack(query, countryMap);

        return results.tracks;

    }
}
