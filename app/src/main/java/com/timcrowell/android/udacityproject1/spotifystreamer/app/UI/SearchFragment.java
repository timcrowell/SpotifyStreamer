package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

        import android.app.Activity;

        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentActivity;
        import android.support.v4.app.FragmentTransaction;
        import android.text.InputType;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.*;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.ArtistSearch;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.SpotifySearch;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItem.SpotifyListItem;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.List.SpotifyListAdapter;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;
        import com.timcrowell.android.udacityproject1.spotifystreamer.app.Util.RetainedFragment;

/**
 * Fragment for searching Spotify.  The search is performed in an EditText at the top
 * and results are populated in a ListView below.  List items are clickable and launch
 * a new fragment to display further results.
 */
public class SearchFragment  extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private FragmentActivity myContext;
    private SpotifyListAdapter spotifyListAdapter;
    private SpotifySearch spotifySearch;

    public SearchFragment(){}

    // This is here to support the Fragment switching code in
    // onCreateView/listView.setOnClickListener
    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showUpButton(false);
        if (Streamer.getInstance() != null) {
            Streamer.getInstance().monitor.forceNotifyObservers();
        }
        super.onResume();
    }

    // Stash the ListAdapter in a safe place so we can keep our search results
    // across device rotations.
    @Override
    public void onStop() {
        super.onStop();
        if (spotifyListAdapter != null) {
            RetainedFragment retainedFragment = RetainedFragment.getInstance();
            retainedFragment.put("SearchFragment_ListAdapter", spotifyListAdapter);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        editText.setHint("Search Artists...");

        // This line makes the Enter button on the keyboard submit the text instead of creating a newline.
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                // When the user submits the query, send it to the Searcher.
                String text = textView.getText().toString();
                spotifySearch.search(text);

                // Remember to keep the new query across device orientation changes.
                RetainedFragment retainedFragment = RetainedFragment.getInstance();
                retainedFragment.put("SearchFragment_ListAdapter", spotifyListAdapter);

                hideKeyboard();

                return true;

            }
        });

        RetainedFragment retainedFragment = RetainedFragment.getInstance();
        if (retainedFragment.containsKey("SearchFragment_ListAdapter")) {

            // Retrieve the ListAdapter if we're restoring from a device rotation
            spotifyListAdapter = (SpotifyListAdapter) retainedFragment.remove("SearchFragment_ListAdapter");

        } else {

            // Otherwise, create a new ListAdapter.
            spotifyListAdapter = new SpotifyListAdapter(getActivity());
        }

        // Attach the ListAdapter to the Searcher
        // TODO - Add logic to make this fragment useful for searching for things other than artists
        spotifySearch = new ArtistSearch(spotifyListAdapter);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_searchresults);

        // Attach the ListAdapter to the ListView
        listView.setAdapter(spotifyListAdapter);

        // Are we a phone or a tablet?
        final boolean isTwoPane;
        if (getActivity().findViewById(R.id.fragment_results_container) == null) {
            isTwoPane = false;
        } else {
            isTwoPane = true;
        }


        // Handle clicks on ListItems here
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyListItem listItem = (SpotifyListItem) view.getTag();

                // Sometimes we use ListItems to display error messages.  They have no action associated with them
                // so we want to avoid doing anything when they get clicked on.
                if (listItem.getType() != SpotifyListItem.Type.MESSAGE) {

                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                    // Create a Fragment to contain results and pass it the query to search for.
                    ResultsFragment resultsFragment = new ResultsFragment();
                    resultsFragment.setQuery(listItem.getId());

                    // For phone layouts
                    if (!isTwoPane) {

                        transaction.replace(R.id.container, resultsFragment, "RESULTS_FRAGMENT");
                        transaction.addToBackStack(null);
                        transaction.commit();

                        // For tablet layouts
                    } else {

                        transaction.replace(R.id.fragment_results_container, resultsFragment, "RESULTS_FRAGMENT");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        });

        return rootView;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = myContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}