package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

/**
 * Interface to derive the various types of list items from.  SpotifyListItem objects
 * are used to keep track of individual search results, hold their metadata, and provide
 * SpotifyListAdapters with the info they need to represent each result on the screen.
 */
public abstract class SpotifyListItemImpl implements SpotifyListItem {

    private String line1;
    private String line2;
    private String image;
    private String id;

    public Object model;

    public abstract boolean hasTwoLines();

    public abstract SpotifyListItem.Type getType();

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getImageUrl() { return image; }

    public Object getModel() { return model; }

    public String getId() {return id; }


    public void setLine1(String line1) { this.line1 = line1; }

    public void setLine2(String line2) { this.line2 = line2; }

    public void setImageUrl(String image) { this.image = image; }

    public void setModel(Object model) { this.model = model; }

    public void setId(String id) { this.id = id; }
}
