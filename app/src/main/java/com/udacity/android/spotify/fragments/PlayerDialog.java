package com.udacity.android.spotify.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.databinding.DialogPlayerBinding;
import com.udacity.android.spotify.models.SpotifyTrack;
import com.udacity.android.spotify.services.MusicPlayService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerDialog extends DialogFragment implements ServiceConnection {

    public final static String TOP_TRACKS = "TOP_TRACKS";
    public final static String TRACK_POSITION = "TRACK_POSITION";
    private final String LOG_TAG = PlayerDialog.class.getSimpleName();

    ArrayList<SpotifyTrack> tracks;
    SpotifyTrack track;
    static SpotifyTrack playing;
    static int position;
    static ArrayList<SpotifyTrack> playingTracks;
    Context context;
    MusicPlayService musicPlayService;
    LocalBroadcastManager localBroadcastManager;

    private DialogPlayerBinding binding;

    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private TextView elapse;
    private TextView trackTime;
    private ProgressBar progressBar;

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
            if (tracks != null)
                track = tracks.get(position);
        }
        setRetainInstance(true);

        Intent bindIntent = new Intent(context, MusicPlayService.class);
        context.startService(bindIntent);
        context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onResume() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Set Dialog dimensions
            // http://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
            int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
            int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
            dialog.getWindow().setLayout(width, height);
            dialog.show();
        }

        localBroadcastManager
                .registerReceiver(receiver, new IntentFilter(MusicPlayService.MEDIA_PLAYER_STATUS));
        localBroadcastManager
                .registerReceiver(receiver, new IntentFilter(MusicPlayService.MEDIA_PLAYER_NEW_TRACK));
        super.onResume();
    }

    @Override
    public void onPause() {
        localBroadcastManager.unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (musicPlayService != null) {
            context.unbindService(this);
        }

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_player, container, false);
        View rootView = binding.getRoot();

        btnPrev = (ImageButton) rootView.findViewById(R.id.btnPrevious);
        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
        elapse = (TextView) rootView.findViewById(R.id.elapse);
        trackTime = (TextView) rootView.findViewById(R.id.track_time);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

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

        updateTrack();

        return rootView;
    }

    private void updateTrack() {
        if (track != null) {
            binding.setTrack(track);

            btnPlay.setImageResource(android.R.drawable.ic_media_play);
            elapse.setText(String.format("%02d:%02d", 0, 0));
            trackTime.setText(String.format("%02d:%02d", 0, 0));
            progressBar.setProgress(0);
        }
    }

    public void playTrack() {
        if (musicPlayService != null) {
            // musicPlayService.setTracks(tracks);
            // musicPlayService.setPosition(position);
            playTrack(position);
        }
    }

    public void playTrack(int position) {
        if (musicPlayService != null) {
            musicPlayService.setTracks(tracks);
            musicPlayService.setPosition(position);
            musicPlayService.playTrack(position);
        }
    }

    public void selectPrev() {
        if (musicPlayService != null)
            musicPlayService.playPrev();
    }

    public void selectNext() {
        if (musicPlayService != null)
            musicPlayService.playNext();
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
        musicPlayService.setTracks(tracks);
        musicPlayService.setPosition(position);
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
                ((ReadyToShare) getActivity()).onReadyToShare();
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
            } else if (intent.getAction().equals(MusicPlayService.MEDIA_PLAYER_NEW_TRACK)) {
                track = playing = intent.getParcelableExtra(MusicPlayService.TRACK_INFO);
                playingTracks = intent.getParcelableArrayListExtra(MusicPlayService.TOP_TRACK_LIST);
                position = intent.getIntExtra(MusicPlayService.TRACK_POSITION, 0);
                updateTrack();
                Log.i(LOG_TAG, "NEW TRACK recorded");
            }
        }
    };

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public interface ReadyToShare {
        void onReadyToShare();
    }
}
