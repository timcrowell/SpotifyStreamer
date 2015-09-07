package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import com.squareup.picasso.Picasso;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
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
    private String trackName = "";
    private String artistName = "";
    private Bitmap albumArt;


    public boolean ready = false;

    public StreamerNotification(Streamer streamer) {

        this.streamer = streamer;
        monitor = streamer.monitor;
        monitor.register(this);
    }


    @Override
    public void update() {

        if (ready) {

            new Thread() {
                @Override
                public void run() {
                    Log.d(TAG, "Album art thread Started.");

                    try {

                        final TrackListItem trackListItem = streamer.controller.getCurrentTrack();
                        trackName = trackListItem.getLine1();
                        artistName = trackListItem.getArtistName();

                        try {
                            albumArt = Picasso.with(streamer.service).load(Uri.parse(trackListItem.getImageUrl())).get();
                        } catch (IOException e) {
                            albumArt = BitmapFactory.decodeResource(streamer.service.getResources(), R.mipmap.ic_launcher);
                        }

                    } catch (NullPointerException e) {

                        trackName = "Select a track.";
                        artistName = "";
                        albumArt = BitmapFactory.decodeResource(streamer.service.getResources(), R.mipmap.ic_launcher);

                    }

                    buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", "ACTION_PLAY"));
                }
            }.start();
        }
    }


    @Override
    public void setSubject(Observable subject) {
        this.monitor = subject;
    }

    public NotificationCompat.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( streamer.service, StreamerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(streamer.service, 1, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }


    public void buildNotification( NotificationCompat.Action action ) {
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

//        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
//        intent.setAction(ACTION_STOP);
//        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(streamer.service);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(trackName);
        builder.setContentText(artistName);
        builder.setLargeIcon(albumArt);
//        builder.setDeleteIntent(pendingIntent);
        builder.setStyle(style);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(streamer.service);
        notificationManager.notify( 1, builder.build() );
    }
}
