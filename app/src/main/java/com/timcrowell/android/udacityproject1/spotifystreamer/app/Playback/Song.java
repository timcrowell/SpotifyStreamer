package com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback;

/**
 * Created by tscrowell on 8/22/15.
 */
public class Song implements Streamable {
    private String trackUrl;
    private String title;
    private String artist;

    public Song (String trackUrl, String songTitle, String songArtist) {
        this.trackUrl = trackUrl;
        this.title = songTitle;
        this.artist = songArtist;
    }

    public String getTrackUrl(){return trackUrl;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}

    public void setTrackUrl(String url){this.trackUrl = url;}


}
