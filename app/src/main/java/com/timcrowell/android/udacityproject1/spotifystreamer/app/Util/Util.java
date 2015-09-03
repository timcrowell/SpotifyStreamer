package com.timcrowell.android.udacityproject1.spotifystreamer.app.Util;

/**
 * Created by tscrowell on 8/24/15.
 */
public class Util {

    public static String getTimeFromMillis(Integer milliseconds) {

        Integer totalSeconds = milliseconds / 1000;
        Integer minutes = totalSeconds / 60;
        Integer remainderSeconds = totalSeconds - 60 * minutes;

        String minutesString;
        String remainderSecondsString;

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = minutes.toString();
        }

        if (remainderSeconds < 10) {
            remainderSecondsString = "0" + remainderSeconds;
        } else {
            remainderSecondsString = remainderSeconds.toString();
        }

        String time = minutesString + ":" + remainderSecondsString;

        return time;
    }
}
