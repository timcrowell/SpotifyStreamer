package com.timcrowell.android.udacityproject1.spotifystreamer.app;

import java.util.List;

/**
 * SpotifyAdapterManager is an interface for classes that are responsible for updating
 * SpotifyListAdapters with new SpotifyListItems.
 *
 * At this point, the only direct descendant of this interface is SpotifySearcher, but there could
 * be additional future implementations. (E.g. for pulling results out of a local database cache
 * rather than connecting to Spotify.)
 */
public interface SpotifyAdapterManager {

    public SpotifyListAdapter getListAdapter();

    public void setListAdapter(SpotifyListAdapter listAdapter);

    public List<SpotifyListItem> getListItems();

}
