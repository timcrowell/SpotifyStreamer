package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.util.Log;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observable;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Observable that pings other classes when the state of StreamerControl changes.
 */
public class StreamerMonitor implements Observable {
    private static final String TAG = StreamerMonitor.class.getSimpleName();

    // WeakReferences are crucial to ensuring old Fragments and Activities get garbage-collected
    // after orientation changes, etc.
    private ArrayList<WeakReference<Observer>> observers = new ArrayList<WeakReference<Observer>>();

    private final Object MUTEX = new Object();

    @Override
    public void register(Observer observer) {
        if (observer == null) { throw new NullPointerException("Attempted to register null Observer."); }
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
        Log.d(TAG, "Notifying Observers.");
        ArrayList<WeakReference<Observer>> mObservers = null;
        synchronized (MUTEX) {
            mObservers = new ArrayList<WeakReference<Observer>>(this.observers);
        }
        for (WeakReference<Observer> weakReferenceObserver : mObservers) {

            if (weakReferenceObserver.get() != null) {
                weakReferenceObserver.get().update();
            } else {
                synchronized (MUTEX) {
                    if (this.observers.contains(weakReferenceObserver)) {
                        this.observers.remove(weakReferenceObserver);
                    }
                }
                Log.d(TAG, "Removed garbage-collected Observer");
            }

        }
    }

    public StreamerMonitor() {
    }

}
