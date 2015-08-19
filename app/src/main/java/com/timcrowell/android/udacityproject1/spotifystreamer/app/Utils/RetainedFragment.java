package com.timcrowell.android.udacityproject1.spotifystreamer.app.Utils;


import android.support.v4.app.Fragment;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Singleton Fragment that stores arbitrary objects in a HashMap so we don't lose
 * them when the screen orientation changes.
 *
 * Beware, the things you store here will never be garbage collected unless you
 * explicitly call remove() on them.  Not a perfect solution, but better than
 * implementing Parcelable.
 */
public class RetainedFragment extends Fragment {
    private static final String TAG = RetainedFragment.class.getSimpleName();

    private HashMap<String, Object> hashMap = new HashMap<String, Object>();

    private static RetainedFragment instance = null;

    // IntelliJ complains about Fragments with private constructors but it still
    // compiles. This is necessary to make sure it can't be instantiated more than once.
    private RetainedFragment(){}

    public static RetainedFragment getInstance() {
        if (instance == null) {
            instance = new RetainedFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This line causes android to not destroy this fragment
        // when the screen orientation changes.
        this.setRetainInstance(true);
    }

    public void put(String key, Object value) {

        if (value != null && key != null) {
            hashMap.put(key, value);
        }
    }

    public Object get(String key) {

        if (key != null) {
            if (hashMap.containsKey(key)) {
                return hashMap.get(key);
            }
        }
        return null;
    }

    public Object remove(String key) {

        if (key != null) {
            if (hashMap.containsKey(key)) {
                return hashMap.remove(key);
            }
        }
        return null;
    }

    public boolean containsKey(String key) {
        if (key != null) {
            return hashMap.containsKey(key);
        }
        return false;
    }
}