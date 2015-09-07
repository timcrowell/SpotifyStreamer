package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;

/**
 * Created by tscrowell on 9/6/15.
 */
public class StreamerNotification {

    private Streamer streamer;

    public StreamerNotification(Streamer streamer) {
        this.streamer = streamer;
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
        builder.setContentTitle("Media Title");
        builder.setContentText("Media Artist");
//        builder.setDeleteIntent(pendingIntent);
        builder.setStyle(style);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(streamer.service);
        notificationManager.notify( 1, builder.build() );
    }
}
