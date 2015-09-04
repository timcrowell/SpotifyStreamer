package com.timcrowell.android.udacityproject1.spotifystreamer.app.Util;

/**
 * Created by tscrowell on 9/3/15.
 */
public interface Observable {

    public void register(Observer observer);
    public void unregister(Observer observer);

    public void notifyObservers();

    public Object getUpdate(Observer observer);

}
