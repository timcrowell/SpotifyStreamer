package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

/**
 * A "dumb" Android service that forwards almost all logic to StreamerControl.
 */
public class StreamerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = StreamerService.class.getSimpleName();

    public StreamerService() {
        Log.d(TAG, "Starting Service.");
    }

    private MediaPlayer player;

    private IBinder playerBinder = new PlayerBinder();

    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Streamer.getInstance().isServiceBound() && Streamer.getInstance().controller != null) {
            Streamer.getInstance().controller.handleIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class PlayerBinder extends Binder {
        StreamerService getService() {
            return StreamerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       return playerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void setSong(Playable playable) {
        Log.d(TAG, "setSong() called.");
        player.reset();
        Uri trackUri = Uri.parse(playable.getTrackUrl());
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }
        Log.d(TAG, "Calling prepareAsync()");
        player.prepareAsync();
    }

    public int getPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pause(){
        player.pause();
    }

    public void seek(int position){
        player.seekTo(position);
    }

    public void start() {player.start();}

    @Override
    public void onCompletion(MediaPlayer mp) {
        Streamer streamer = Streamer.getInstance();
        streamer.controller.onStreamerCompleted();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Streamer streamer = Streamer.getInstance();
        streamer.monitor.notifyObservers();
        Log.d(TAG, "OnError reached.");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        Log.d(TAG, "Informing the StreamerController we're ready.");
        Streamer streamer = Streamer.getInstance();
        streamer.controller.onStreamerPrepared();
    }
}

