package com.timcrowell.android.udacityproject1.spotifystreamer.app.ListMakers;

import android.os.AsyncTask;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.SpotifyListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.SpotifyListItemFactory;
import retrofit.RetrofitError;

import java.util.ArrayList;
import java.util.List;

/**
 * SpotifySearcher provides a wrapper around AsyncTask and is used by the classes that extend
 * it to run network code on a background thread, convert the results to SpotifyListItems,
 * and update a SpotifyListAdapter accordingly.
 */
public abstract class SpotifySearcher implements SpotifyAdapterManager {
    private static final String TAG = SpotifySearcher.class.getSimpleName();

    private SpotifySearchTask spotifySearchTask;
    private SpotifyListAdapter listAdapter;
    private String query;
    private List<SpotifyListItem> resultsList = new ArrayList<SpotifyListItem>();

    @Override
    public SpotifyListAdapter getListAdapter() {
        return listAdapter;
    }

    @Override
    public void setListAdapter(SpotifyListAdapter listAdapter) {this.listAdapter = listAdapter; }

    @Override
    public List<SpotifyListItem> getListItems() {return resultsList; }

    public SpotifySearcher(SpotifyListAdapter listAdapter){
        this.listAdapter = listAdapter;
    }

    public void search(String query){
        this.query = query;
        spotifySearchTask = new SpotifySearchTask();
        spotifySearchTask.execute();
    }

    // The reason for making this a nested class is that AsyncTask is limited to single arguments, but
    // we need to pass it both a query and a SpotifyListAdapter.  Having it nested like this allows it
    // to access the fields of it's parent directly, avoiding the problem.
    private class SpotifySearchTask extends AsyncTask<Void, Void, List> {
        private final String TAG = SpotifySearchTask.class.getSimpleName();

        @Override
        protected List doInBackground(Void... voids) {

            try {

                // This method gets overridden by subclasses and contains networking code.
                return queryToPerform(query);

            } catch (RetrofitError error) {

                // Can't connect to Spotify.  Will generate error message in onPostExecute.
                return null;
            }
        }

        // Once the networking code is run, convert the results into SpotifyListItems.
        @Override
        protected void onPostExecute(List objects) {

            resultsList.clear();


            if (objects == null) {

                // If the connection failed, give the user an error message.
                resultsList.add(SpotifyListItemFactory.createMessage("Unable to Connect to Spotify."));

            } else {

                for (Object object : objects) {
                    resultsList.add(SpotifyListItemFactory.convertApiObjectToSpotifyListItem(object));
                }

                // If the connection was successful, but the search returned no results, let the user know.
                if (objects.isEmpty()) {
                    resultsList.add(SpotifyListItemFactory.createMessage("No Results Found."));
                }
            }

            updateListAdapter();
        }
    }

    // Update the SpotifyListAdapter with the new SpotifyListItems.
    private void updateListAdapter(){

        listAdapter.clear();

        for (SpotifyListItem listItem: resultsList) {
                listAdapter.add(listItem);
        }
    }

    // Subclasses must override this with their networking code.
    abstract List queryToPerform(String query);

}

