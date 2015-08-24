package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

/**
 * Object that represents Artists and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class ArtistListItem extends SpotifyListItem {

    @Override
    public SpotifyListItem.Type getType() { return Type.ARTIST; }

    @Override
    public boolean hasTwoLines() {
        return false;
    }
}
