package com.udacity.android.spotify;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerDialog extends DialogFragment {

    public final static String TOP_TRACKS = "TOP_TRACKS";
    public final static String TRACK_POSITION = "TRACK_POSITION";

    ArrayList<SpotifyTrack> tracks;
    static SpotifyTrack track;
    static int position;
    static int playing;
    static MediaPlayer mediaPlayer;
    double length;
    double progress;
    private Handler handler = new Handler();;

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
    @InjectView(R.id.elapse)
    TextView elapse;
    @InjectView(R.id.track_time)
    TextView trackTime;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    public PlayerDialog() {
        // Required empty public constructor
    }

    public static PlayerDialog newInstance(ArrayList<SpotifyTrack> tracks, int position) {
        PlayerDialog frag = new PlayerDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TOP_TRACKS, tracks);
        args.putInt(TRACK_POSITION, position);
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
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tracks = getArguments().getParcelableArrayList(TOP_TRACKS);
        position = getArguments().getInt(TRACK_POSITION);
        track = tracks.get(position);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.fragment_player, container);
        ButterKnife.inject(this, view);

        updateTrack();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrack();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = (position > 0) ? position - 1 : tracks.size() - 1;
                track = tracks.get(position);
                updateTrack();
                if (playing != position) {
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = (position < tracks.size() - 1) ? position + 1 : 0;
                track = tracks.get(position);
                updateTrack();
                if (playing != position) {
                    btnPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        return view;
    }

    private void updateTrack() {
        artistName.setText(" " + track.artistName);
        albumName.setText(" " + track.albumName);
        trackName.setText(" " + track.trackName);
        Picasso.with(getActivity()).load(track.profileImage).into(image);
    }

    public void loadAndPlay(final int pos) {
        track = tracks.get(pos);
        String url = track.uri;

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                length = player.getDuration();
                progress = player.getCurrentPosition();
                Log.i("INFO", "Current Duration " + length);
                Log.i("INFO", "Current Position " + progress);
                elapse.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) progress),
                                TimeUnit.MILLISECONDS.toSeconds((long) progress) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) progress)))
                );
                trackTime.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) length),
                                TimeUnit.MILLISECONDS.toSeconds((long) length) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) length)))
                );

                player.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                playing = pos;
            }
        });

    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            progress = mediaPlayer.getCurrentPosition();
            elapse.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) progress),
                            TimeUnit.MILLISECONDS.toSeconds((long) progress) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) progress)))
            );
            progressBar.setProgress((int) progress);
            handler.postDelayed(this, 100);
        }
    };

    public void playTrack() {
        if (mediaPlayer.isPlaying()) {
            // Pause the track
            mediaPlayer.pause();
            btnPlay.setImageResource(android.R.drawable.ic_media_play);

            // Switch to a new track? Load and play
            if (position != playing) {
                mediaPlayer.reset();
                loadAndPlay(position);
            }

        } else {
            if (position == playing) {
                // Resume the same track after pause, no need to load new track
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                handler.postDelayed(UpdateSongTime,100);
            } else {
                // Starting a new track, load and play
                mediaPlayer.reset();
                loadAndPlay(position);
            }
        }

        // Log.i("INFO", mediaPlayer.)
    }
}
