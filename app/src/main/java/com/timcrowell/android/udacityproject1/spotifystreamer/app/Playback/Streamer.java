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
    public StreamerController controller;

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
            Log.d(TAG, "createInstance() called.");
            instance = new Streamer(context);
            Log.d(TAG, "Got to end of creating the instance");
            if (instance == null) {
                Log.d(TAG, "Instance is still null");
            }
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
                controller = new StreamerController(instance);
            }
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