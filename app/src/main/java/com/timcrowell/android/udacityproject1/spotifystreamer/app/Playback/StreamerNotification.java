package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.squareup.picasso.Picasso;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.UI.MainActivity;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observable;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.Observer;

import java.io.IOException;

/**
 * Created by tscrowell on 9/6/15.
 */
public class StreamerNotification implements Observer{
    private static final String TAG = StreamerNotification.class.getSimpleName();

    private Streamer streamer;
    private Observable monitor;
    private String trackName;
    private String artistName;
    private Bitmap albumArt;
    private int smallIcon;
    private boolean hasPlayed = false;

    public StreamerNotification(Streamer streamer) {

        this.streamer = streamer;
        monitor = streamer.monitor;
        monitor.register(this);
    }


    @Override
    public void update() {

        // This is to make sure the notification doesn't launch before everything has been initialized.
        if (!hasPlayed && streamer.controller != null && streamer.controller.isPlaying()) {
            hasPlayed = true;
        }
        if (hasPlayed) {

            // Check to make sure we're supposed to show notifications.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(streamer.service.getBaseContext());
            Boolean showNotification = sharedPreferences.getBoolean("showNotification", true);
            if (showNotification) {

                new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Updating notification on background thread.");

                        if (!streamer.controller.isStopped()) {

                            final TrackListItem trackListItem = streamer.controller.getCurrentTrack();
                            trackName = trackListItem.getLine1();
                            artistName = trackListItem.getArtistName();

                            if (streamer.controller.isPlaying()) {
                                smallIcon = R.drawable.ic_play;
                            } else {
                                smallIcon = R.drawable.ic_pause;
                            }

                            try {
                                albumArt = Picasso.with(streamer.service).load(Uri.parse(trackListItem.getImageUrl())).get();
                            } catch (IOException e) {
                                albumArt = BitmapFactory.decodeResource(streamer.service.getResources(), R.mipmap.ic_launcher);
                            }

                            postNotification();

                        } else {

                            // Remove notification when player stops.
                            remove();
                        }
                    }
                }.start();
            }
        }
    }


    @Override
    public void setSubject(Observable subject) {
        this.monitor = subject;
    }


    private NotificationCompat.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent(streamer.service, StreamerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(streamer.service, 1, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }


    public void postNotification() {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(streamer.service);
        builder.setSmallIcon(smallIcon);
        builder.setContentTitle(trackName);
        builder.setContentText(artistName);
        builder.setLargeIcon(albumArt);
        builder.setStyle(style);

        Intent contentIntent = new Intent(streamer.service, MainActivity.class);
        contentIntent.setAction(MainActivity.ACTION_DISPLAYPLAYER);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(streamer.service.getApplicationContext(), 1, contentIntent, 0);
        builder.setContentIntent(pendingContentIntent);

        Intent deleteIntent = new Intent(streamer.service, StreamerService.class);
        deleteIntent.setAction(StreamerControl.ACTION_STOP);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(streamer.service, 1, deleteIntent, 0);
        builder.setDeleteIntent(pendingDeleteIntent);

        builder.addAction(generateAction(R.drawable.ic_rewind, "Previous", StreamerControl.ACTION_PREVIOUS));

        if (streamer.controller.isPlaying()) {
            builder.addAction(generateAction(R.drawable.ic_pause, "Pause", StreamerControl.ACTION_PAUSE));
        } else {
            builder.addAction(generateAction(R.drawable.ic_play, "Play", StreamerControl.ACTION_PLAY));
        }

        builder.addAction(generateAction(R.drawable.ic_fastforward, "Next", StreamerControl.ACTION_NEXT));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(streamer.service);
        Notification notification = builder.build();
        notificationManager.notify( 1, notification );
        streamer.service.startForeground(1, notification);
    }

    public void remove() {
        Log.d(TAG, "Removing Notification");
        streamer.service.stopForeground(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(streamer.service);
        notificationManager.cancel(1);
    }

}
