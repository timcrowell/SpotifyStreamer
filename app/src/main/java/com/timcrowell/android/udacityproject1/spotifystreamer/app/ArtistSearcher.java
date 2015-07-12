package com.timcrowell.android.udacityproject1.spotifystreamer.app;

import android.util.Log;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

import java.util.List;

/**
 * Searches Spotify for Artists and populates a SpotifyListAdapter with the results.
 */
public class ArtistSearcher extends SpotifySearcher {
    private static final String TAG = ArtistSearcher.class.getSimpleName();

    public ArtistSearcher(SpotifyListAdapter adapter) {
        super(adapter);
    }

    // This method runs on a background thread.
    @Override
    List queryToPerform(String query) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        ArtistsPager results = spotify.searchArtists(query);

        return results.artists.items;

    }
}
