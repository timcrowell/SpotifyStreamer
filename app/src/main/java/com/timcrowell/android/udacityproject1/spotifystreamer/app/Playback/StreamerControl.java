package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.content.Intent;
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
    private boolean isStopped = true;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";


    public void handleIntent(Intent intent) {
        Log.d(TAG, "Handling Intent");
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            start();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            pause();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            previous();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            next();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            stop();
        }
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void notifyCompleted() {
        next();
    }

    @Override
    public boolean isPlaying() {
        return (isServicePlaying() || shouldPlay);
    }


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
            int progress = 100 * getCurrentPosition() / duration;
            return progress;
        } else {
            return 0;
        }
    }

    @Override
    public void setProgress(Integer progress) {

        seekTo(progress * getDuration() / 100);
        streamer.monitor.refresh();
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
        Log.d(TAG, "Calling setSong()");
        streamer.service.setSong(currentSong);
        playerIsPrepared = false;
        songIsLoaded = true;
        streamer.monitor.refresh();
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

        if (playerIsPrepared && getProgress() > 1) {

            setProgress(0);

        } else {

            if (songIndex > 0) {

                setListItem(songIndex - 1);

            } else {

                Log.d(TAG, "At beginning of playlist.");
                stop();

            }
        }
    }

    @Override
    public void next() {
        Log.d(TAG, "Next called.");

        if (playlist.size() > songIndex + 1) {
            setListItem(songIndex + 1);
        } else {
            Log.d(TAG, "End of playlist.");
            stop();
            setListItem(0);
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "Stop called.");
        shouldPlay = false;
        streamer.service.pause();
        isStopped = true;

        if (getProgress() != 0) {
            setProgress(0);
        } else {
            setListItem(0);
        }
    }

    @Override
    public void start() {
        Log.d(TAG, "Start called.");
        Log.d(TAG, "ShouldPlay: " + shouldPlay + " IsStopped: " + isStopped + " PlayerIsPreppared: " + playerIsPrepared +
                " isPlaying: " + streamer.service.isPlaying() + " songIsLoaded: " + songIsLoaded);
        if(songIsLoaded) {
            shouldPlay = true;
            isStopped = false;
            if (playerIsPrepared) {
                streamer.service.start();
            }
            streamer.monitor.refresh();
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause called");
        shouldPlay = false;
        if (playerIsPrepared && isServicePlaying()) {
            streamer.service.pause();
        }
        streamer.monitor.refresh();
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


    private boolean isServicePlaying() {
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
