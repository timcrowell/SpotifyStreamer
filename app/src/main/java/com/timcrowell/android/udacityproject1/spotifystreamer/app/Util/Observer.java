package com.timcrowell.android.udacityproject1.spotifystreamer.app.Util;

public interface Observer {

    public void update();

    public void setSubject(Observable subject);
}