package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.util.Log;
import android.widget.MediaController;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Util;

import java.util.List;

/**
 * Created by tscrowell on 8/23/15.
 */
public class StreamerControl implements MediaController.MediaPlayerControl, PlaylistControl {
    private static final String TAG = StreamerControl.class.getSimpleName();

    private Streamer streamer;
    private List<TrackListItem> playlist;
    private int songIndex = 0;
    private Playable currentSong;
    private boolean songIsLoaded = false;
    private boolean playerIsPrepared = false;
    private boolean shouldPlay = false;

    public StreamerControl(Streamer streamer) {
        this.streamer = streamer;
    }

    public void onPlayerPrepared() {
        Log.d(TAG, "Prepared");
        playerIsPrepared = true;
        if (shouldPlay) {
            start();
        }
    }


    public String getPositionTime() {
        Integer posMilli = streamer.controller.getCurrentPosition();
        if (posMilli != 0) {
            return Util.getTimeFromMillis(posMilli);
        } else {
            return "00:00";
        }
    }

    public String getDurationTime() {
        Integer durMilli = streamer.controller.getDuration();
        if (durMilli != 0) {
            return Util.getTimeFromMillis(durMilli);
        } else {
            return "00:00";
        }
    }

    @Override
    public Integer getProgress() {
        Integer duration = getDuration();
        if (duration != 0) {
            return 100 * getCurrentPosition() / duration;
        } else {
            return 0;
        }
    }

    @Override
    public void setProgress(Integer progress) {
        seekTo(progress * getDuration() / 100);
    }

    @Override
    public void setPlaylist(List<TrackListItem> playlist) {
        if ( playlist != null) {
            this.playlist = playlist;
            setListItem(0);
        } else {
            Log.e(TAG, "setPlaylist() Playlist is null.");
        }
    }

    @Override
    public void setListItem(int index) {
        Log.d(TAG, "Playlist index is: " + index);
        songIndex = index;
        currentSong = playlist.get(songIndex);
        streamer.service.setSong(currentSong);
        playerIsPrepared = false;
        songIsLoaded = true;
    }

    @Override
    public TrackListItem getCurrentTrack() {
        if (playlist != null) {
            return playlist.get(songIndex);
        } else {
            Log.d(TAG, "Playlist is null.");
            return null;
        }
    }

    @Override
    public void previous() {

        Log.d(TAG, "Prev Called.");

        boolean wasPlaying = shouldPlay && isPlaying();

        if (playerIsPrepared && getCurrentPosition() != 0) {

            pause();
            setProgress(0);

        } else {

            if (songIndex > 0) {

                setListItem(songIndex - 1);

            } else {

                Log.d(TAG, "At beginning of playlist.");

            }
        }
        if (wasPlaying) { start(); }
    }

    @Override
    public void next() {
        Log.d(TAG, "Next called.");

        if (playlist.size() > songIndex) {

            boolean wasPlaying = shouldPlay && isPlaying();

            setListItem(songIndex + 1);

            if (wasPlaying) { shouldPlay = true;}

        } else {
            Log.d(TAG, "End of playlist.");
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "Stop called.");
        if (isPlaying()) {
            pause();
            setProgress(0);
        } else if (getProgress() == 0) {
            setProgress(0);
        } else {
            setListItem(0);
        }
    }

    @Override
    public void start() {
        Log.d(TAG, "Start called.");
        if(songIsLoaded) {
            shouldPlay = true;
            if (playerIsPrepared) {
                streamer.service.start();
            }
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause called");
        shouldPlay = false;
        if (playerIsPrepared && isPlaying()) {
            streamer.service.pause();
        }
    }

    @Override
    public int getDuration() {
        if (songIsLoaded && playerIsPrepared) {
            return streamer.service.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (streamer.isServiceBound()) {
                return streamer.service.getPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        if (songIsLoaded && playerIsPrepared && streamer.service.getPosition() != i) {
            streamer.service.seek(i);
        }
    }

    @Override
    public boolean isPlaying() {
        if (streamer.isServiceBound()) {
            return  streamer.service.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
