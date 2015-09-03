package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem;

/**
 * Object that represents Artists and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class ArtistListItem extends SpotifyListItemBase {

    @Override
    public SpotifyListItem.Type getType() { return Type.ARTIST; }

    @Override
    public boolean hasTwoLines() {
        return false;
    }
}
