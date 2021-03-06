package com.udacity.android.spotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.SpotifyApplication;
import com.udacity.android.spotify.fragments.PlayerDialog;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlayerActivity extends ActionBarActivity implements PlayerDialog.ReadyToShare {
    PlayerDialog playerDialog;
    ArrayList<SpotifyTrack> mTracks;
    int mPosition;
    private ShareActionProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        if (intent != null) {
            mTracks = intent.getParcelableArrayListExtra(PlayerDialog.TOP_TRACKS);
            mPosition = intent.getIntExtra(PlayerDialog.TRACK_POSITION, 0);
        }

        if (savedInstanceState == null) {
            if (mTracks != null && mTracks.size() > 0) {
                playerDialog = PlayerDialog.newInstance(mTracks, mPosition);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, playerDialog)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupShareIntent() {
        SpotifyTrack track = SpotifyApplication.getAppTrack();
        URL url = null;
        try {
            url = new URL(track.getUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // Create share intent as described above
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, url);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/html");
        // Attach share event to the menu item provider
        if (provider != null)
            provider.setShareIntent(shareIntent);
    }

    @Override
    public void onReadyToShare() {
        setupShareIntent();
    }
}
