package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems;

import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Playable;

/**
 * Object that represents Tracks and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class TrackListItem extends SpotifyListItem implements Playable {

    private String trackUrl;
    private int playlistIndex;

    @Override
    public boolean hasTwoLines() {
        return true;
    }

    @Override
    public SpotifyListItem.Type getType() {
        return Type.TRACK;
    }

    public int getPlaylistIndex() {
        return playlistIndex;
    }

    public void setPlaylistIndex(int playlistIndex) {
        this.playlistIndex = playlistIndex;
    }

    @Override
    public String getTrackUrl() {
        return trackUrl;
    }

    @Override
    public void setTrackUrl(String url) {
        this.trackUrl = url;
    }

}