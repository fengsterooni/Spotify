package com.udacity.android.spotify.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.SpotifyApplication;
import com.udacity.android.spotify.fragments.PlayerDialog;
import com.udacity.android.spotify.fragments.TopTrackActivityFragment;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.util.ArrayList;


public class TopTrackActivity extends ActionBarActivity {
    static TopTrackActivityFragment fragment;
    public final static String ARTIST_ID = "ARTIST_ID";
    public final static String ARTIST_NAME = "ARTIST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_track);

        String artistID = getIntent().getStringExtra(ARTIST_ID);

        // get artist name and set it as sub title on the action bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Intent intent = getIntent();
            String artistName = intent.getStringExtra(ARTIST_NAME);
            if (artistName != null) {
                getSupportActionBar().setSubtitle(artistName);
            }
        }

        if (savedInstanceState == null) {
            fragment = TopTrackActivityFragment.newInstatnce(artistID);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_toptrack, fragment, MainActivity.TOPTRACK_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_track, menu);
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

        if (id == R.id.action_playing) {
            ArrayList<SpotifyTrack> cTracks = SpotifyApplication.getAppTracks();
            int cPosition = SpotifyApplication.getAppPosition();
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra(PlayerDialog.TOP_TRACKS, cTracks);
            intent.putExtra(PlayerDialog.TRACK_POSITION, cPosition);
            startActivity(intent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
