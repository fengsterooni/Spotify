package com.udacity.android.spotify.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.activities.TopTrackActivity;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayService extends Service {
    static MediaPlayer mediaPlayer;
    static SpotifyTrack mTrack;
    static ArrayList<SpotifyTrack> mTracks;
    static int mPosition;
    private static Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();
    LocalBroadcastManager broadcastManager;
    public static final String MEDIA_PLAYER_STATUS = "MEDIA_PLAYER_STATUS";
    public static final String MEDIA_PLAYER_NEW_TRACK = "MEDIA_PLAYER_NEW_TRACK";
    public static final String TRACK_PROGRESS = "TRACK_PROGRESS";
    public static final String TRACK_DURATION = "TRACK_DURATION";
    public static final String TRACK_STATUS = "TRACK_STATUS";
    public static final String TRACK_INFO = "TRACK_INFO";

    public static final String TRACK_POSITION = "TRACK_POSITION";

    public static String PREV_ACTION = "com.udacity.android.spotify.action.prev";
    public static String PLAY_ACTION = "com.udacity.android.spotify.action.play";
    public static String NEXT_ACTION = "com.udacity.android.spotify.action.next";

    public static final int NOTIFICATION_ID = 101;
    boolean finished = false;

    public MusicPlayService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        broadcastManager = LocalBroadcastManager.getInstance(this);
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

    public void setTracks(ArrayList<SpotifyTrack> tracks) {
        mTracks = tracks;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(PLAY_ACTION)) {
                int num = intent.getIntExtra(TRACK_POSITION, 0);
                Log.i("INFO", "GOT POSITION: " + num);
                playTrack(num);
            }

            if (action != null && action.equals(PREV_ACTION)) {
                mPosition = intent.getIntExtra(TRACK_POSITION, 0);
                Log.i("INFO", "GOT POSITION: " + mPosition);
                playTrack(mPosition);
            }

            if (action != null && action.equals(NEXT_ACTION)) {
                mPosition = intent.getIntExtra(TRACK_POSITION, 0);
                Log.i("INFO", "GOT POSITION: " + mPosition);
                playTrack(mPosition);
            }
        }

        return super.onStartCommand(intent, flags, startId);
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
                Intent intent = new Intent(MEDIA_PLAYER_NEW_TRACK);
                intent.putExtra(TRACK_INFO, mTrack);
                intent.putExtra(TRACK_POSITION, mPosition);
                broadcastManager.sendBroadcast(intent);
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

    public void playTrack(int position) {
        if (mTracks != null) {
            mPosition = position;
            playTrack(mTracks.get(position));
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //int visibility = pref.getInt(getString(R.string.pref_notifications_visibilty_key), 0);
        //int visibility = pref.getInt(getString(R.string.pref_notifications_visibilty_key), 0);

        Resources resources = getResources();
        int drawableID = android.R.drawable.ic_media_play;
        int stringID = R.string.notification_play;
        if (mediaPlayer.isPlaying()) {
            drawableID = android.R.drawable.ic_media_pause;
            stringID = R.string.notification_pause;
        }

        Intent notificationIntent = new Intent(this, TopTrackActivity.class);
        // notificationIntent.putExtra(PlayerDialog.TOP_TRACKS, mTracks);
        // notificationIntent.putExtra(PlayerDialog.TRACK_POSITION, mPosition);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayService.class);
        playIntent.putExtra(TRACK_POSITION, mPosition);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0,
                playIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent prevIntent = new Intent(this, MusicPlayService.class);
        prevIntent.putExtra(TRACK_POSITION, selectPrev());
        prevIntent.setAction(PREV_ACTION);
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 0,
                prevIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent nextIntent = new Intent(this, MusicPlayService.class);
        nextIntent.putExtra(TRACK_POSITION, selectNext());
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        /*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(visibility)
                .setContentTitle(title)
                .setContentText(text)
                        //.setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,
                        resources.getString(R.string.notification_previous),
                        prevplayIntent)
                .addAction(mediaPlayer.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                        mediaPlayer.isPlaying() ? resources.getString(R.string.notification_pause) : resources.getString(R.string.notification_play),
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next,
                        resources.getString(R.string.notification_next),
                        nextplayIntent)
                .setAutoCancel(true);
        */

        Notification notification = new NotificationCompat.Builder(this)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.spotify_white)
                        // Add media control buttons that invoke intents in your media service
                .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent) // #0
                .addAction(android.R.drawable.ic_media_pause, "Pause", pausePendingIntent)  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)     // #2
                        // Apply the media style template
                .setStyle(new NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1 /* #1: pause button */))
                        //.setMediaSession(mMediaSession.getSessionToken())
                        .setContentTitle(title)
                        .setContentText(text)
                        //.setLargeIcon(R.mipmap.ic_launcher)
                        .build();
        return notification;
    }

    public int selectPrev() {
        int position = mPosition;
        return (position > 0) ? position - 1 : mTracks.size() - 1;
    }

    public int selectNext() {
        int position = mPosition;
        return (position < mTracks.size() - 1) ? position + 1 : 0;
    }

    public void playNext() {
        mPosition = selectNext();
        play();
    }

    public void playPrev() {
        mPosition = selectPrev();
        play();
    }

    public void play() {
        playTrack(mPosition);
    }
}
