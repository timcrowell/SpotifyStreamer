package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

/**
 * Object that can be inserted into SpotifyListAdapters to provide a message
 * to the user from within the list. Instantiated by SpotifyListItemFactory
 */
public class MessageListItem extends SpotifyListItem {

    @Override
    public Type getType() { return Type.MESSAGE; }

    @Override
    public boolean hasTwoLines() {
        return false;
    }
}
