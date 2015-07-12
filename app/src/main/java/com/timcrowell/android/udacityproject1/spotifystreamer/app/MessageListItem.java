package com.timcrowell.android.udacityproject1.spotifystreamer.app;

/**
 * Object that can be inserted into SpotifyListAdapters to provide a message
 * to the user from within the list. Instantiated by SpotifyListItemFactory
 */
public class MessageListItem implements SpotifyListItem {

    private String line1;
    private String line2;
    private String image;
    private String id;

    public Object model;


    @Override
    public boolean hasTwoLines() {
        return false;
    }

    @Override
    public Type getType() { return Type.MESSAGE; }


    @Override
    public String getLine1() {
        return line1;
    }

    @Override
    public String getLine2() {
        return line2;
    }

    @Override
    public String getImageUrl() { return image; }

    @Override
    public Object getModel() { return model; }

    @Override
    public String getId() {return id; }


    @Override
    public void setLine1(String line1) { this.line1 = line1; }

    @Override
    public void setLine2(String line2) { this.line2 = line2; }

    @Override
    public void setImageUrl(String image) { this.image = image; }

    @Override
    public void setModel(Object model) { this.model = model; }

    @Override
    public void setId(String id) { this.id = id; }
}
