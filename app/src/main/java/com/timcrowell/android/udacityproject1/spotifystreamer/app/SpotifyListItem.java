package com.timcrowell.android.udacityproject1.spotifystreamer.app;

/**
 * Interface to derive the various types of list items from.  SpotifyListItem objects
 * are used to keep track of individual search results, hold their metadata, and provide
 * SpotifyListAdapters with the info they need to represent each result on the screen.
 */
public interface SpotifyListItem {

    public String getLine1();
    public String getLine2();
    public String getImageUrl();
    public Object getModel();
    public String getId();

    public boolean hasTwoLines();
    public Type getType();

    public void setLine1(String line1);
    public void setLine2(String line2);
    public void setImageUrl(String image);
    public void setModel(Object model);
    public void setId(String id);

    public enum Type {
        ARTIST,
        ALBUM,
        TRACK,
        MESSAGE
    }
}
