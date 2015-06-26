package com.udacity.android.spotify;

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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayerDialog extends DialogFragment implements ServiceConnection {

    public final static String TOP_TRACKS = "TOP_TRACKS";
    public final static String TRACK_POSITION = "TRACK_POSITION";

    ArrayList<SpotifyTrack> tracks;
    static SpotifyTrack track;
    static int position;
    static int playing;
    Context context;
    MusicPlayService musicPlayService;

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

        Intent bindIntent = new Intent(context, MusicPlayService.class);
        context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, new IntentFilter(MusicPlayService.MEDIA_PLAYER_STATUS));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        if (musicPlayService != null) {
            context.unbindService(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        tracks = getArguments().getParcelableArrayList(TOP_TRACKS);
        position = getArguments().getInt(TRACK_POSITION);
        track = tracks.get(position);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_player, container);
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
        artistName.setText(" " + track.artistName);
        albumName.setText(" " + track.albumName);
        trackName.setText(" " + track.trackName);
        Picasso.with(getActivity()).load(track.profileImage).into(image);

        if (position != playing) {
            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            elapse.setText(String.format("%02d:%02d", 0, 0));
            trackTime.setText(String.format("%02d:%02d", 0, 0));
            progressBar.setProgress(0);
        }
    }

    public void playTrack() {
        if (musicPlayService != null) {
            musicPlayService.playTrack(track);
            playing = position;
        }
    }

    public void selectPrev() {
        position = (position > 0) ? position - 1 : tracks.size() - 1;
        track = tracks.get(position);
        updateTrack();
    }

    public void selectNext() {
        position = (position < tracks.size() - 1) ? position + 1 : 0;
        track = tracks.get(position);
        updateTrack();
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
            if (playing == position) {
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
    };
}
