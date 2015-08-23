package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by tscrowell on 8/22/15.
 */
public class StreamController implements PlayController {
    private static final String TAG = StreamController.class.getSimpleName();

    private StreamingService streamingService;
    private Intent playIntent;
    private boolean playerBound = false;
    private Context appContext;
    private static StreamController instance;

    private StreamController(Context context){
        this.appContext = context;
        playIntent = new Intent(appContext, StreamingService.class);
        appContext.bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
        appContext.startService(playIntent);
    }


    public static StreamController getInstance(Context context) {
        if (instance == null) {
            instance = new StreamController(context);
        }
        return instance;
    }

    public static StreamController getInstance() {
        if (instance == null) {
            return null;
        }
        return instance;
    }

    private ServiceConnection playerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StreamingService.PlayerBinder binder = (StreamingService.PlayerBinder) iBinder;
            streamingService = binder.getService();
            playerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playerBound = false;
        }
    };

    public void setSong(Streamable streamable) {

        streamingService.setSong(streamable);
    }

    public void playSong() {
        streamingService.playSong();
    }

}
