package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

/**
 * Interface to derive the various types of list items from.  SpotifyListItem objects
 * are used to keep track of individual search results, hold their metadata, and provide
 * SpotifyListAdapters with the info they need to represent each result on the screen.
 */
public interface SpotifyListItem {

    String getLine1();
    String getLine2();
    String getImageUrl();
    Object getModel();
    String getId();

    boolean hasTwoLines();
    Type getType();

    void setLine1(String line1);
    void setLine2(String line2);
    void setImageUrl(String image);
    void setModel(Object model);
    void setId(String id);

    public enum Type {
        ARTIST,
        ALBUM,
        TRACK,
        MESSAGE
    }
}
