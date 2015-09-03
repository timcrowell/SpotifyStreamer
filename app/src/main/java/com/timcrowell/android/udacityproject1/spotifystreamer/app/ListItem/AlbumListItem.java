package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem;

/**
 * Object that represents Albums and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class AlbumListItem extends SpotifyListItemBase {

    @Override
    public SpotifyListItem.Type getType() { return Type.ALBUM; }

    @Override
    public boolean hasTwoLines() {
        return true;
    }

}
