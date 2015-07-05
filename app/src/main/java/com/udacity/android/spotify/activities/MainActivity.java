package com.udacity.android.spotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.SpotifyApplication;
import com.udacity.android.spotify.fragments.PlayerDialog;
import com.udacity.android.spotify.fragments.SearchFragment;
import com.udacity.android.spotify.fragments.TopTrackActivityFragment;

public class MainActivity extends ActionBarActivity
        implements SearchFragment.OnItemSelected {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String TOPTRACK_TAG = "TOPTRACK_TAG";
    public static final String PLAYER_TAG = "PLAYER_TAG";
    private static boolean mTwoPane;

    public static boolean ismTwoPane() {
        return mTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_toptrack) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_playing) {
                FragmentManager fm = getSupportFragmentManager();
                PlayerDialog playerDialog =
                        PlayerDialog.newInstance(
                                SpotifyApplication.getAppTracks(),
                                SpotifyApplication.getAppPosition()
                        );

                if (MainActivity.ismTwoPane())
                    playerDialog.show(fm, PLAYER_TAG);
                else {
                    startActivity(new Intent(this, PlayerActivity.class));
                }

            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String artistID, String artistName) {
        if (mTwoPane) {
            TopTrackActivityFragment fragment = TopTrackActivityFragment.newInstatnce(artistID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_toptrack, fragment, TOPTRACK_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTrackActivity.class);
            intent.putExtra(TopTrackActivity.ARTIST_ID, artistID);
            intent.putExtra(TopTrackActivity.ARTIST_NAME, artistName);
            startActivity(intent);
        }
    }
}
