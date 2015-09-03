package com.timcrowell.android.udacityproject1.spotifystreamer.app.List;

import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.SpotifyListItem;

import java.util.List;

/**
 * SpotifyListAdapterProvider is an interface for classes that are responsible for updating
 * SpotifyListAdapters with new SpotifyListItems.
 *
 * At this point, the only direct descendant of this interface is SpotifySearch, but there could
 * be additional future implementations. (E.g. for pulling results out of a local database cache
 * rather than connecting to Spotify.)
 */
public interface SpotifyListAdapterProvider {

    SpotifyListAdapter getListAdapter();

    void setListAdapter(SpotifyListAdapter listAdapter);

    List<SpotifyListItem> getListItems();

}
