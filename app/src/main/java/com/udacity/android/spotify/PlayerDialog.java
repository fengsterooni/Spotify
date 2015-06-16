package com.udacity.android.spotify;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerDialog extends DialogFragment {
    List<SpotifyTrack> tracks;
    @InjectView(R.id.artist_name)
    TextView artistName;
    @InjectView(R.id.album_name)
    TextView albumName;
    @InjectView(R.id.track_name)
    TextView trackName;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.btnPrevious)
    ImageButton btnPrev;
    @InjectView(R.id.btnPlay)
    ImageButton btnPlay;
    @InjectView(R.id.btnNext)
    ImageButton btnNext;

    public PlayerDialog() {
        // Required empty public constructor
    }

    public static PlayerDialog newInstance(SpotifyTrack track) {
        PlayerDialog frag = new PlayerDialog();
        Bundle args = new Bundle();
        args.putParcelable("track", track);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set Dialog dimensions
        // http://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        SpotifyTrack track = getArguments().getParcelable("track");
        View view = inflater.inflate(R.layout.fragment_player, container);
        ButterKnife.inject(this, view);

        artistName.setText(" " + track.artistName);
        albumName.setText(" " + track.albumName);
        trackName.setText(" " + track.trackName);

        Picasso.with(getActivity()).load(track.profileImage).into(image);

        stream(track);

        return view;
    }


    public void stream(SpotifyTrack track) {
        String url = track.uri;

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                player.start();
            }
        });
    }
}
