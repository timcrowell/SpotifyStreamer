package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.SpotifyListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
import kaaes.spotify.webapi.android.models.Track;

import java.util.List;

/**
 * SpotifyListAdapters convert SpotifyListItems into a list of views to be diplayed in a ListView.
 * Adding removing items is usually performed by a SpotifyAdapterManager like ArtistSearcher.
 */
public class SpotifyListAdapter extends ArrayAdapter<SpotifyListItem> {
    private static final String TAG = SpotifyListAdapter.class.getSimpleName();

    private Activity context;
    private List<TrackListItem> playListItems;

    public void setPlaylistItems(List<TrackListItem> items) {
        this.playListItems = items;
        if (playListItems == null) {
            Log.d(TAG, "setplaylistitems() Playlist is null.");
        } else {
            Log.d(TAG, "setplaylistitems() Playlist is not null.");
        }
        if (items == null) {
            Log.d(TAG, "setplaylistitems() ITEMS is null.");
        } else {
            Log.d(TAG, "setplaylistitems() ITEMS is not null.");
        }
    }

    public List<TrackListItem> getPlaylistItems() { return this.playListItems; }

    public SpotifyListAdapter(Activity context) {
        super(context, 0);
        this.context = context;
    }

    // This getView method runs inside a ForEach loop, so the code inside is running per list item.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SpotifyListItem spotifyListItem = getItem(position);

        // If the listItem isn't a message (e.g. error message), choose the appropriate layout.
        // for the music item it represents.
        if (spotifyListItem.getType() != SpotifyListItem.Type.MESSAGE) {

            if (spotifyListItem.hasTwoLines()) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_spotify_twoline, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_spotify_oneline, parent, false);
            }

        } else {

            // If the list Item is a message, use a special layout with no image.
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_spotify_message, parent, false);
        }


        // If the list item isn't a message, load the appropriate data into each of the layout's Views.
        if (spotifyListItem.getType() != SpotifyListItem.Type.MESSAGE){

            ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            Picasso.with(context).load(Uri.parse(spotifyListItem.getImageUrl())).into(iconView);

            TextView line1TextView = (TextView) convertView.findViewById(R.id.list_item_text_line1);
            line1TextView.setText(spotifyListItem.getLine1());

            if (spotifyListItem.hasTwoLines()) {
                TextView line2TextView = (TextView) convertView.findViewById(R.id.list_item_text_line2);
                line2TextView.setText(spotifyListItem.getLine2());
            }

        } else {

            // If the list item is a message, load the message into line1.
            TextView line1TextView = (TextView) convertView.findViewById(R.id.list_item_text_line1);
            line1TextView.setText(spotifyListItem.getLine1());
        }

        // Views don't contain metadata on the items they represent, so we stash the SpotifyListItem object
        // into the tag field, that way we can later get things like the artist id of the item the user clicks on.
        convertView.setTag(spotifyListItem);

        return convertView;
    }

}
