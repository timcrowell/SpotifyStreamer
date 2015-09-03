package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem;

/**
 * Created by tscrowell on 8/24/15.
 */
public interface SpotifyListItem {

    public enum Type {
        ARTIST,
        ALBUM,
        TRACK,
        MESSAGE
    }

    public abstract boolean hasTwoLines();

    public SpotifyListItem.Type getType();

    public String getLine1();

    public String getLine2();

    public String getImageUrl();

    public Object getModel();

    public String getId();


    public void setLine1(String line1);

    public void setLine2(String line2);

    public void setImageUrl(String image);

    public void setModel(Object model);

    public void setId(String id);
}
