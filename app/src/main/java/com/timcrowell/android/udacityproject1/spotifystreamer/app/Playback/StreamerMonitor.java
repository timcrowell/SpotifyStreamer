package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.util.Log;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observable;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by tscrowell on 9/3/15.
 */
public class StreamerMonitor implements Observable {
    private static final String TAG = StreamerMonitor.class.getSimpleName();

    private Streamer streamer;
    private ArrayList<WeakReference<Observer>> observers = new ArrayList<WeakReference<Observer>>();

    private boolean isPlaying = false;
    private boolean changed = false;

    private final Object MUTEX = new Object();

    @Override
    public void register(Observer observer) {
        if (observer == null) { throw new NullPointerException("Null Observer Registered"); }
        synchronized (MUTEX) {
            if(!observers.contains(observer)) {
                observers.add(new WeakReference<Observer>(observer));
            }
        }
    }

    @Override
    public void unregister(Observer observer) {
        synchronized (MUTEX) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        ArrayList<WeakReference<Observer>> mObservers = null;
        synchronized (MUTEX) {
            if (!changed) { return; }
            mObservers = new ArrayList<WeakReference<Observer>>(this.observers);
            this.changed = false;
        }
        for (WeakReference<Observer> weakReferenceObserver : mObservers) {
            weakReferenceObserver.get().update();
        }
    }

    @Override
    public Object getUpdate(Observer observer) {
        return null;
    }


    public StreamerMonitor(Streamer streamer) {
        this.streamer = streamer;
    }

    public void fetchChanges() {
        boolean wasPlaying = isPlaying;
        boolean nowPlaying = streamer.service.isPlaying();

        if (nowPlaying != wasPlaying) {
            this.isPlaying = nowPlaying;
            this.changed = true;
            notifyObservers();
        }

    }


}
