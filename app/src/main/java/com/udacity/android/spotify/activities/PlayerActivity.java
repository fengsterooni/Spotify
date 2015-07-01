package com.udacity.android.spotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.spotify.fragments.PlayerDialog;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.util.ArrayList;

public class PlayerActivity extends ActionBarActivity {
    PlayerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<SpotifyTrack> tracks = intent.getParcelableArrayListExtra(PlayerDialog.TOP_TRACKS);
            int position = intent.getIntExtra(PlayerDialog.TRACK_POSITION, 0);

            if (savedInstanceState == null) {
                dialog = PlayerDialog.newInstance(tracks, position);
                if (dialog != null) {
                    if (MainActivity.ismTwoPane()) {
                        dialog.show(getSupportFragmentManager(), "Player");
                    } else
                        getSupportFragmentManager().beginTransaction()
                                .add(android.R.id.content, dialog)
                                .commit();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
