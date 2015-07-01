package com.udacity.android.spotify.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.models.SpotifyTrack;
import com.udacity.android.spotify.services.MusicPlayService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerDialog extends DialogFragment implements ServiceConnection {

    public final static String TOP_TRACKS = "TOP_TRACKS";
    public final static String TRACK_POSITION = "TRACK_POSITION";

    ArrayList<SpotifyTrack> tracks;
    static SpotifyTrack track;
    SpotifyTrack playing;
    static int position;
    Context context;
    MusicPlayService musicPlayService;
    LocalBroadcastManager localBroadcastManager;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        Bundle args = getArguments();
        if (args != null) {
            tracks = args.getParcelableArrayList(TOP_TRACKS);
            position = args.getInt(TRACK_POSITION);
            track = tracks.get(position);
        }
        setRetainInstance(true);

        Intent bindIntent = new Intent(context, MusicPlayService.class);
        context.startService(bindIntent);
        context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog = getDialog();
        if (dialog != null) {
            // Set Dialog dimensions
            // http://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
            int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
            int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
            dialog.getWindow().setLayout(width, height);
        }

        localBroadcastManager
                .registerReceiver(receiver, new IntentFilter(MusicPlayService.MEDIA_PLAYER_STATUS));
    }

    @Override
    public void onPause() {
        localBroadcastManager.unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayService != null) {
            context.unbindService(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_player, container, false);
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
                selectPrev();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNext();
            }
        });

        return view;
    }

    private void updateTrack() {
        if (track != null) {
            artistName.setText(" " + track.getArtistName());
            albumName.setText(" " + track.getAlbumName());
            trackName.setText(" " + track.getTrackName());
            Picasso.with(getActivity()).load(track.getProfileImage()).into(image);

            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            elapse.setText(String.format("%02d:%02d", 0, 0));
            trackTime.setText(String.format("%02d:%02d", 0, 0));
            progressBar.setProgress(0);
        }
    }

    public void playTrack() {
        if (musicPlayService != null) {
            musicPlayService.setTracks(tracks);
            playTrack(position);
        }
    }

    public void playTrack(int position) {
        if (musicPlayService != null) {
            musicPlayService.playTrack(position);
        }
    }

    public void selectPrev() {
        position = (position > 0) ? position - 1 : tracks.size() - 1;
        track = tracks.get(position);
        updateTrack();
        playTrack(position);
    }

    public void selectNext() {
        position = (position < tracks.size() - 1) ? position + 1 : 0;
        track = tracks.get(position);
        updateTrack();
        playTrack(position);
    }

    // http://stackoverflow.com/questions/12433397/android-dialogfragment-disappears-after-orientation-change
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicPlayService = ((MusicPlayService.LocalBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicPlayService = null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicPlayService.MEDIA_PLAYER_STATUS)) {
                playing = intent.getParcelableExtra(MusicPlayService.TRACK_INFO);
                if (playing.getId().equals(track.getId())) {
                    double progress = intent.getDoubleExtra(MusicPlayService.TRACK_PROGRESS, 0.0);
                    double duration = intent.getDoubleExtra(MusicPlayService.TRACK_DURATION, 0.0);
                    elapse.setText(String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes((long) progress),
                                    TimeUnit.MILLISECONDS.toSeconds((long) progress) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes((long) progress)))
                    );
                    trackTime.setText(String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes((long) duration),
                                    TimeUnit.MILLISECONDS.toSeconds((long) duration) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes((long) duration)))
                    );
                    progressBar.setProgress((int) (progress * 100 / duration));

                    boolean isPlaying = intent.getBooleanExtra(MusicPlayService.TRACK_STATUS, false);
                    if (isPlaying)
                        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                    else
                        btnPlay.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        }
    };

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
