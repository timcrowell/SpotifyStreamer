package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Playable;

/**
 * Object that represents Tracks and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class TrackListItem implements SpotifyListItem, Playable {

    private String line1;
    private String line2;
    private String image;
    private String id;
    private String trackUrl;
    private int playlistIndex;

    public Object model;

    public TrackListItem() {
    }

    public int getPlaylistIndex() {
        return playlistIndex;
    }

    public void setPlaylistIndex(int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    @Override
    public boolean hasTwoLines() { return true; }

    @Override
    public SpotifyListItem.Type getType() { return Type.TRACK; }


    @Override
    public String getLine1() { return line1; }

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
    public String getTrackUrl() { return trackUrl; }

    @Override
    public void setTrackUrl(String url) { this.trackUrl = url; }

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
