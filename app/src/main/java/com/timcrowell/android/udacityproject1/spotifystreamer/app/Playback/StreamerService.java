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

public class StreamerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = StreamerService.class.getSimpleName();

    public StreamerService() {
        Log.d(TAG, "Constructor called");
    }

    private MediaPlayer player;
    private Playable playable;

    private IBinder playerBinder = new PlayerBinder();

    public void onCreate() {
        super.onCreate();

        player = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
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

        this.playable = playable;

        player.reset();

        Uri trackUri = Uri.parse(playable.getTrackUrl());

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }

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
        streamer.monitor.refresh();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Streamer streamer = Streamer.getInstance();
        streamer.monitor.refresh();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        Streamer streamer = Streamer.getInstance();
        streamer.controller.onPlayerPrepared();
        streamer.monitor.refresh();
    }


}

