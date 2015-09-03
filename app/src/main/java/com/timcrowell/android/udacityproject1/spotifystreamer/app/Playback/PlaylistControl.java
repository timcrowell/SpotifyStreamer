package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;


import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;

import java.util.List;

/**
 * Created by tscrowell on 8/23/15.
 */
public interface PlaylistControl {


    public void previous();
    public void next();
    public void stop();
    public void setPlaylist(List<TrackListItem> playlist);
    public TrackListItem getCurrentTrack();
    public void setListItem(int index);
    public Integer getProgress();
    public void setProgress(Integer progress);

    // Redundant with interface android.widget.MediaController.MediaPlayerControl
    public void start();
    public void pause();
    public int getDuration();
    public int getCurrentPosition();
    public void seekTo(int i);
    public boolean isPlaying();

}
