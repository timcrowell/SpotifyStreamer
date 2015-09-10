package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.content.Intent;
import android.util.Log;
import android.widget.MediaController;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Util;

import java.util.List;

/**
 * Keeps track of the state of the StreamerService and provides methods to control playback.
 */
public class StreamerControl implements MediaController.MediaPlayerControl, PlaylistControl {
    private static final String TAG = StreamerControl.class.getSimpleName();

    private Streamer streamer;
    private List<TrackListItem> playlist;
    private int songIndex = 0;
    private Playable currentSong;
    private boolean songIsLoaded = false;
    private boolean streamerIsPrepared = false;
    private boolean shouldPlay = false;
    private boolean isStopped = true;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    public StreamerControl(Streamer streamer) {
        this.streamer = streamer;
    }

    // These intents are coming from the notification controls.
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


    // Gets called from StreamerService.onCompletion when the end of the track is reached.
    public void onStreamerCompleted() {
        next();
    }

    // Returns true if music is playing or buffering getting ready to play.
    @Override
    public boolean isPlaying() {
        return (isServicePlaying() || shouldPlay);
    }

    // Gets called from StreamerService.onPrepared when the service is finished buffering the stream.  All
    // playback should be start()ed from here.
    public void onStreamerPrepared() {
        Log.d(TAG, "Prepared");
        streamerIsPrepared = true;
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

    // Returns a number out of 100 for how far along the playback is.
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

    // Seeks to a location out of 100.
    @Override
    public void setProgress(Integer progress) {
        seekTo(progress * getDuration() / 100);
        streamer.monitor.notifyObservers();
    }

    // The list of tracks comes in here from the ResultsFragment
    @Override
    public void setPlaylist(List<TrackListItem> playlist) {
        if ( playlist != null) {
            this.playlist = playlist;
            setListItem(0);
        } else {
            Log.d(TAG, "setPlaylist(): Playlist should not be empty");
        }
    }

    // Chooses a track in the playlist and tells the service to go fetch it.
    @Override
    public void setListItem(int index) {
        Log.d(TAG, "Playlist index is: " + index);
        songIndex = index;
        currentSong = playlist.get(songIndex);
        Log.d(TAG, "Calling setSong()");
        streamer.service.setSong(currentSong);

        // This is set to false so that the song doesn't play until the streamer is finished
        // buffering and calls back to onStreamerPrepared
        streamerIsPrepared = false;

        songIsLoaded = true;
        streamer.monitor.notifyObservers();
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

        // The first time we hit back, it goes back to the beginning of the track.
        if (streamerIsPrepared && getProgress() > 1) {
            setProgress(0);

            // If we double-click, we go to the previous track...
        } else {
            if (songIndex > 0) {
                setListItem(songIndex - 1);

                // ...unless we can't, in which case we stop.
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

        // The first time you tap it, stop() seeks to the beginning of the track.
        if (getProgress() != 0) {
            setProgress(0);

            // Ths second time, it goes to the beginning of the playlist.
        } else {
            setListItem(0);
        }
    }

    /* Implements the play button functionality, but doesn't actually play anything unless the streamerIsPrepared.
     * To prepare the streamer, use setListItem(), which will cause this method to eventually be called from
     * onStreamerPrepared so long as shouldPlay=true.
     */
    @Override
    public void start() {
        Log.d(TAG, "Start called.");
        Log.d(TAG, "ShouldPlay: " + shouldPlay + " IsStopped: " + isStopped + " PlayerIsPreppared: " + streamerIsPrepared +
                " isPlaying: " + streamer.service.isPlaying() + " songIsLoaded: " + songIsLoaded);
        if(songIsLoaded) {
            shouldPlay = true;
            isStopped = false;
            if (streamerIsPrepared) {
                streamer.service.start();
            }
            streamer.monitor.notifyObservers();
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause called");
        shouldPlay = false;
        if (streamerIsPrepared && isServicePlaying()) {
            streamer.service.pause();
        }
        streamer.monitor.notifyObservers();
    }

    @Override
    public int getDuration() {
        if (songIsLoaded && streamerIsPrepared) {
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
        if (songIsLoaded && streamerIsPrepared && streamer.service.getPosition() != i) {
            streamer.service.seek(i);
        }
    }

    // Returns true if audio is coming out of the speakers, but not if the song is still buffering.
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
