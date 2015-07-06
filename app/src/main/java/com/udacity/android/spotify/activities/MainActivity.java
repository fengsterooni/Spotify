package com.udacity.android.spotify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.SpotifyApplication;
import com.udacity.android.spotify.fragments.PlayerDialog;
import com.udacity.android.spotify.fragments.SearchFragment;
import com.udacity.android.spotify.fragments.TopTrackActivityFragment;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity
        implements SearchFragment.OnItemSelected, PlayerDialog.ReadyToShare {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String TOPTRACK_TAG = "TOPTRACK_TAG";
    public static final String PLAYER_TAG = "PLAYER_TAG";
    private ShareActionProvider provider;

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

        if (id == R.id.action_playing) {
            ArrayList<SpotifyTrack> cTracks = SpotifyApplication.getAppTracks();
            int cPosition = SpotifyApplication.getAppPosition();

            if (cTracks != null && cTracks.size() > 0) {
                FragmentManager fm = getSupportFragmentManager();
                PlayerDialog playerDialog =
                        PlayerDialog.newInstance(cTracks, cPosition);

                if (MainActivity.ismTwoPane()) {
                    playerDialog.show(fm, PLAYER_TAG);
                } else {
                    Intent intent = new Intent(this, PlayerActivity.class);
                    intent.putExtra(PlayerDialog.TOP_TRACKS, cTracks);
                    intent.putExtra(PlayerDialog.TRACK_POSITION, cPosition);
                    startActivity(intent);
                }
                return true;
            } else
                new MaterialDialog.Builder(this)
                        .title(R.string.no_music_title)
                        .content(R.string.no_music_message)
                        .positiveText(R.string.OK)
                        .show();
        }

        if (id == R.id.action_share) {
            return false;
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
