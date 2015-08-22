package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;


public class PlayerFragment extends DialogFragment implements PlayerViewContract {
    private static final String TAG = PlayerFragment.class.getSimpleName();


    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);



    }
}
