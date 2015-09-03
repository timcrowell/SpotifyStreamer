package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem;

import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Playable;

/**
 * Object that represents Tracks and stores their relevant metadata.
 * Instantiated by SpotifyListItemFactory
 */
public class TrackListItem extends SpotifyListItemBase implements Playable {

    private String trackUrl;
    private int playlistIndex;
    private String artistName;

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

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String url) {
        this.trackUrl = url;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}