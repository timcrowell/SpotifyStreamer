package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.nio.channels.NotYetBoundException;

/**
 * This singleton class gets called by MainActivity when the app launches and sets up the bound service for music
 * playback.  It also initializes all the other related streamer classes and holds reference to them.
 */
public class Streamer {
    private static final String TAG = Streamer.class.getSimpleName();

    public StreamerService service;
    public StreamerControl controller;
    public StreamerMonitor monitor = new StreamerMonitor();
    public StreamerNotification notification = new StreamerNotification(this);

    private Intent playIntent;
    private Context appContext;
    private static Streamer instance;
    private boolean serviceBound = false;

    private Streamer(Context context){
        this.appContext = context;

        playIntent = new Intent(appContext, StreamerService.class);
        appContext.bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
        appContext.startService(playIntent);
    }

    public static Streamer getInstance(Context context) {
        if (instance == null) {
            instance = new Streamer(context);
        }
        return instance;
    }

    public static Streamer getInstance() throws NotYetBoundException {
        if (instance == null) {
            Log.d(TAG, "Streamer called before it was initialized.");
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
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "StreamerService Disconnected.");
            serviceBound = false;
        }
    };

    public boolean isServiceBound() {
        return serviceBound;
    }

}
