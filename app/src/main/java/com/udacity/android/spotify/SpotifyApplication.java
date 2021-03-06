package com.udacity.android.spotify;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.udacity.android.spotify.models.SpotifyTrack;
import com.udacity.android.spotify.services.MusicPlayService;
import com.udacity.android.spotify.utils.ImageUtils;

import java.util.ArrayList;

public class SpotifyApplication extends Application {
    private static ArrayList<SpotifyTrack> appTracks;
    private static String appArtistID;
    private static SpotifyTrack appTrack;
    private static int appPosition;
    private static boolean isPlaying;
    LocalBroadcastManager localBroadcastManager;

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static SpotifyTrack getAppTrack() {
        return appTrack;
    }

    public static int getAppPosition() {
        return appPosition;
    }

    public static ArrayList<SpotifyTrack> getAppTracks() {
        return appTracks;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ImageUtils.setupMemoryCache();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager
                .registerReceiver(receiver, new IntentFilter(MusicPlayService.MEDIA_PLAYER_NEW_TRACK));
    }

    @Override
    public void onTerminate() {
        localBroadcastManager.unregisterReceiver(receiver);
        super.onTerminate();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicPlayService.MEDIA_PLAYER_NEW_TRACK)) {
                appTrack = intent.getParcelableExtra(MusicPlayService.TRACK_INFO);
                appTracks = intent.getParcelableArrayListExtra(MusicPlayService.TOP_TRACK_LIST);
                appPosition = intent.getIntExtra(MusicPlayService.TRACK_POSITION, 0);
                if (appTrack != null)
                    isPlaying = true;
            }
        }
    };
}
