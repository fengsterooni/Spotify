package com.udacity.android.spotify;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class MusicPlayService extends Service {
    static MediaPlayer mediaPlayer;
    static SpotifyTrack mTrack;
    private static Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();
    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    public static final String MEDIA_PLAYER_STATUS = "MEDIA_PLAYER_STATUS";
    public static final String TRACK_PROGRESS = "TRACK_PROGRESS";
    public static final String TRACK_DURATION = "TRACK_DURATION";
    public static final String TRACK_STATUS = "TRACK_STATUS";
    public static final String PLAYING_TRACK = "PLAYING_TRACK";
    public static final int NOTIFICATION_ID = 101;

    public MusicPlayService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    public void loadAndPlay(final SpotifyTrack track) {
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
                player.start();
                mTrack = track;
                handler.postDelayed(UpdateTrack, 100);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(UpdateTrack);
                double duration = mediaPlayer.getDuration();
                Intent intent = new Intent(MEDIA_PLAYER_STATUS);
                intent.putExtra(TRACK_STATUS, mediaPlayer.isPlaying());
                intent.putExtra(TRACK_PROGRESS, duration);
                intent.putExtra(TRACK_DURATION, duration);
                broadcastManager.sendBroadcast(intent);
            }
        });

        startForeground(NOTIFICATION_ID, buildNotification());
    }

    public void playTrack(SpotifyTrack track) {
        if (mediaPlayer.isPlaying()) {
            // Pause the track
            mediaPlayer.pause();

            // Switch to a new track? Load and play
            if (track != mTrack) {
                handler.removeCallbacks(UpdateTrack);
                mediaPlayer.reset();
                loadAndPlay(track);
            }

        } else {
            if (track == mTrack) {
                // Resume the same track after pause, no need to load new track
                mediaPlayer.start();
            } else {
                // Starting a new track, load and play
                mediaPlayer.reset();
                loadAndPlay(track);
            }
        }
    }

    private Runnable UpdateTrack = new Runnable() {
        public void run() {
            if (mediaPlayer != null) {
                double progress = mediaPlayer.getCurrentPosition();
                double duration = mediaPlayer.getDuration();
                Intent intent = new Intent(MEDIA_PLAYER_STATUS);
                intent.putExtra(TRACK_STATUS, mediaPlayer.isPlaying());
                intent.putExtra(TRACK_PROGRESS, progress);
                intent.putExtra(TRACK_DURATION, duration);
                broadcastManager.sendBroadcast(intent);
                handler.postDelayed(this, 100);
            }
        }
    };

    private Notification buildNotification() {
        Notification notification = new Notification();
        // notification.
        return notification;
    }
}
