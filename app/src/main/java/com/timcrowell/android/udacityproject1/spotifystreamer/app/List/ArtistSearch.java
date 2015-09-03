package com.timcrowell.android.udacityproject1.spotifystreamer.app.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;

import java.util.List;

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

        ArtistsPager results = spotify.searchArtists(query);

        return results.artists.items;

    }
}
