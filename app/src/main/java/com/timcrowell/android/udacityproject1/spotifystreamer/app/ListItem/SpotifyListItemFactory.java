package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem;

import android.util.Log;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Class with static methods for producing SpotifyListItems. Utilized by
 * SpotifyAdapterManagers like ArtistSearch.
 */
public class SpotifyListItemFactory {
    private static final String TAG = SpotifyListItemFactory.class.getSimpleName();

    // We use SpotifyListItems to provide a common interface to SpotifyListAdapters
    // regardless of whether it's an artist/album/etc.  The Spotify API doesn't return
    // SpotifyListItems, it has it's own "models" that don't implement a common interface
    // so we convert them into our own objects here.
    public static SpotifyListItem convertApiObjectToSpotifyListItem(Object object) {


        if (object instanceof Artist) {

            Artist model = (Artist) object;

            ArtistListItem listItem= new ArtistListItem();
            listItem.setLine1(model.name);
            listItem.setId(model.id);
            listItem.setModel(model);

            try {
                listItem.setImageUrl(model.images.get(0).url);
            } catch (IndexOutOfBoundsException e) {
                listItem.setImageUrl("");
                Log.d(TAG, "No image found for " + model.name);
            }

            return listItem;

        } else if (object instanceof AlbumSimple) {

            AlbumSimple model = (AlbumSimple) object;

            AlbumListItem listItem= new AlbumListItem();
            listItem.setLine1(model.name);

            // Looks like you can't get the artist of an album from the Spotify Api
            // TODO - Find out if there's a fix / workaround for this limitation
            listItem.setLine2("<artist goes here>");

            listItem.setId(model.id);
            listItem.setModel(model);

            try {
                listItem.setImageUrl(model.images.get(0).url);
            } catch (IndexOutOfBoundsException e) {
                listItem.setImageUrl("");
                Log.d(TAG, "No image found for " + model.name);
            }

            return listItem;

        } else if (object instanceof Track) {

            Track model = (Track) object;

            TrackListItem listItem = new TrackListItem();
            listItem.setLine1(model.name);
            listItem.setLine2(model.album.name);
            listItem.setId(model.id);
            listItem.setModel(model);
            listItem.setTrackUrl(model.preview_url);
            listItem.setArtistName(model.artists.get(0).name);

            try {
                listItem.setImageUrl(model.album.images.get(0).url);
            } catch (IndexOutOfBoundsException e) {
                listItem.setImageUrl("");
                Log.d(TAG, "No image found for " + model.album.name);
            }

            return listItem;
        }

        return null;

    }

    // We can use MessageListItems to put a message as one of the elements in the ListView
    // Can be useful for error messages or maybe section headers, e.g. "Top 5 Albums:"
    public static SpotifyListItem createMessage(String messageText) {

        SpotifyListItem listItem = new MessageListItem();

        listItem.setLine1(messageText);
        listItem.setLine2("");
        listItem.setImageUrl("");
        listItem.setId("");
        listItem.setImageUrl("");

        return listItem;
    }

}
