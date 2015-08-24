package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.util.Log;
import android.widget.MediaController;

import java.util.List;

/**
 * Created by tscrowell on 8/23/15.
 */
public class StreamerController implements MediaController.MediaPlayerControl, PlaylistController {
    private static final String TAG = StreamerController.class.getSimpleName();

    private Streamer streamer;
    private List<Playable> playlist;
    private int songIndex = 0;
    private Playable currentSong;
    private int seekLocation = 0;
    private boolean isSongLoaded = false;

    public StreamerController(Streamer streamer) {
        this.streamer = streamer;
    }


    @Override
    public void setPlayList(List<? extends Playable> playlist) {
        if ( playlist != null) {
            this.playlist = (List<Playable>) playlist;
            setListItem(0);
        } else {
            Log.e(TAG, "setPLaylist() Playlist is null.");
        }
    }

    @Override
    public void setListItem(int index) {
        Log.d(TAG, "Playlist index is: " + index);
        if (songIndex != index) { songIndex = index; }
        currentSong = playlist.get(songIndex);
        seekTo(0);
    }

    @Override
    public void previous() {

        boolean wasPlaying = isPlaying();

        if (getCurrentPosition() != 0) {

            if (isPlaying()) { pause();}
            seekTo(0);

        } else {
            if (songIndex > 0) {

                if (isPlaying()) { pause();}
                setListItem(songIndex - 1);

            } else {

                Log.d(TAG, "At beginning of playlist.");

            }
        }
        if (wasPlaying) { start(); }
    }

    @Override
    public void next() {
        if (playlist.size() > songIndex) {

            boolean wasPlaying = isPlaying();

            if (isPlaying()) {
                pause();
                setListItem(songIndex + 1);
            }

            if (wasPlaying) { start(); }

        } else {
            Log.d(TAG, "End of playlist.");
        }
    }

    @Override
    public void stop() {
        if (isPlaying()) {
            pause();
            seekTo(0);
        } else {
            setListItem(0);
        }
    }

    @Override
    public void start() {

        streamer.service.setSong(currentSong);
        isSongLoaded = true;
        seekTo(seekLocation);
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            seekLocation = streamer.service.getPosition();
            streamer.service.pause();
        }
    }

    @Override
    public int getDuration() {
        if (streamer.isServiceBound()) {
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
        if (isSongLoaded && streamer.isServiceBound() && streamer.service.getPosition() != i) {
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
