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

public class StreamingService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = StreamingService.class.getSimpleName();

    public StreamingService() {
    }

    private MediaPlayer player;
    private Streamable streamable;

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
        StreamingService getService() {
            return StreamingService.this;
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

    public void setSong(Streamable streamable) {

        this.streamable = streamable;
    }

    public void playSong() {
        player.reset();

        Uri trackUri = Uri.parse(streamable.getTrackUrl());

        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
        }

        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }


}

