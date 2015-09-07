package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.nio.channels.NotYetBoundException;

/**
 * Created by tscrowell on 8/22/15.
 */
public class Streamer {
    private static final String TAG = Streamer.class.getSimpleName();

    public StreamerService service;
    public StreamerControl controller;
    public StreamerMonitor monitor = new StreamerMonitor(this);
    public StreamerNotification notification = new StreamerNotification(this);

    private Intent playIntent;
    private Context appContext;
    private static Streamer instance;
    private boolean serviceBound = false;

    private Streamer(Context context){
        Log.d(TAG, "Made it to the constructor.");
        this.appContext = context;

        playIntent = new Intent(appContext, StreamerService.class);
        appContext.bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
        appContext.startService(playIntent);

        Log.d(TAG, "Got to end of creating the constructor");
    }

    public static Streamer getInstance(Context context) {
        if (instance == null) {
            instance = new Streamer(context);
        }
        return instance;
    }

    public static Streamer getInstance() throws NotYetBoundException {
        if (instance == null) {
            Log.e(TAG, "Hit the fan.");
            return null;
        }
        return instance;
    }

    private ServiceConnection playerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StreamerService.PlayerBinder binder = (StreamerService.PlayerBinder) iBinder;
            service = binder.getService();
            serviceBound = true;
            if (controller == null) {
                controller = new StreamerControl(instance);
            }
            notification.buildNotification(notification.generateAction(android.R.drawable.ic_media_play, "Play", "ACTION_PLAY"));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };

    public boolean isServiceBound() {
        return serviceBound;
    }

}
