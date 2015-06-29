package com.udacity.android.spotify.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.models.SpotifyTrack;

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
    public static final String TRACK_INFO = "TRACK_INFO";
    public static final int NOTIFICATION_ID = 101;
    boolean finished = false;

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
        stopForeground(true);
        mTrack = null;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }

    public void loadAndPlay(final SpotifyTrack track) {
        String url = track.getUri();

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
                startForeground(NOTIFICATION_ID, buildNotification());
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(UpdateTrack);
                double duration = mediaPlayer.getDuration();
                Intent intent = new Intent(MEDIA_PLAYER_STATUS);
                intent.putExtra(TRACK_INFO, mTrack);
                intent.putExtra(TRACK_STATUS, mediaPlayer.isPlaying());
                intent.putExtra(TRACK_PROGRESS, 0.0);
                intent.putExtra(TRACK_DURATION, duration);
                broadcastManager.sendBroadcast(intent);
                finished = true;
            }
        });
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
                // If track is completed, and user wanted to play again, reset to update progress again
                if (finished) {
                    handler.postDelayed(UpdateTrack, 100);
                    finished = false;
                }
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
                intent.putExtra(TRACK_INFO, mTrack);
                intent.putExtra(TRACK_STATUS, mediaPlayer.isPlaying());
                intent.putExtra(TRACK_PROGRESS, progress);
                intent.putExtra(TRACK_DURATION, duration);
                broadcastManager.sendBroadcast(intent);
                handler.postDelayed(this, 100);
            }
        }
    };

    private Notification buildNotification() {
        String title = "";
        String text = "";
        if (mTrack != null) {
            title = mTrack.getArtistName();
            text = mTrack.getTrackName();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                //.setContentIntent(pIntent)
                ;

        builder.setAutoCancel(true);

        return builder.build();
    }
}
