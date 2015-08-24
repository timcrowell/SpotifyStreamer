package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;


import java.util.List;

/**
 * Created by tscrowell on 8/23/15.
 */
public interface PlaylistController {


    public void previous();
    public void next();
    public void stop();
    public void setPlayList(List<Playable> playlist);
    public void setListItem(int index);

    // Redundant with interface android.widget.MediaController.MediaPlayerControl
    public void start();
    public void pause();
    public int getDuration();
    public int getCurrentPosition();
    public void seekTo(int i);
    public boolean isPlaying();

}
