package com.timcrowell.android.udacityproject1.spotifystreamer.app.UI;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.ListItems.TrackListItem;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.Playback.Streamer;
import com.timcrowell.android.udacityproject1.spotifystreamer.app.R;


public class PlayerFragment extends DialogFragment implements PlayerViewContract {
    private static final String TAG = PlayerFragment.class.getSimpleName();

    Context myContext;
    View myView;
    TrackListItem currentTrack;
    Streamer streamer = Streamer.getInstance();
    SeekBar seekBar;
    Thread seekBarUpdaterThread;
    Boolean fragmentIsVisible = false;
    String positionText = "00:00";


    public void startSeekBarUpdater() {
        if (seekBarUpdaterThread == null) {

            seekBarUpdaterThread = new Thread() {

                @Override
                public void run() {

                    while (fragmentIsVisible) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "seekUpdater thread interrupted.");
                        }
                        seekBar.setProgress(streamer.controller.getProgress());
                        positionText = streamer.controller.getPositionTime();
                    }
                }
            };
            seekBarUpdaterThread.start();
        }
    }


    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        this.myView = view;

        final Streamer streamer = Streamer.getInstance();

        refreshView();



        // TODO find a different way to make the position TextView refresh automatically
        // If I do this like the seekbar, android complains about UI on background thread
//        final TextView positionTextView = (TextView) view.findViewById(R.id.positionText);
//        TextView durationText = (TextView) view.findViewById(R.id.durationText);
//        durationText.setText(streamer.controller.getDurationTime());

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        this.seekBar = seekBar;
        fragmentIsVisible = true;
        startSeekBarUpdater();


        final Integer seekPosition = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Streamer streamer = Streamer.getInstance();

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(myContext, "Seek " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
                streamer.controller.setProgress(seekBar.getProgress());
            }
        });

        final View prevButton = view.findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamer.controller.previous();
                refreshView();
            }
        });

        final View playButton = view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamer.controller.start();
            }
        });

        final View pauseButton = view.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamer.controller.pause();
            }
        });

        final View nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamer.controller.next();
                refreshView();
            }
        });




        return view;
    }

    private void refreshView() {

        Streamer streamer = Streamer.getInstance();
        currentTrack = streamer.controller.getCurrentTrack();

        final TextView artistText = (TextView) myView.findViewById(R.id.artistName);
        artistText.setText(currentTrack.getArtistName());

        final TextView trackText = (TextView) myView.findViewById(R.id.trackName);
        trackText.setText(currentTrack.getLine1());

        ImageView albumArtView = (ImageView) myView.findViewById(R.id.albumArt);
        Picasso.with(myContext).load(Uri.parse(currentTrack.getImageUrl())).into(albumArtView);

    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentIsVisible = false;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
