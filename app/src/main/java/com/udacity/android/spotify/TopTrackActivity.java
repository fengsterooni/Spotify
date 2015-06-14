package com.udacity.android.spotify;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TopTrackActivity extends ActionBarActivity {
    TopTrackActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_track);

        String artistID = getIntent().getStringExtra("id");

        // get artist name and set it as sub title on the action bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Intent intent = getIntent();
            String artistName = intent.getStringExtra("artist_name");
            if (artistName != null) {
                getSupportActionBar().setSubtitle(artistName);
            }
        }

        if (savedInstanceState == null) {
            fragment = TopTrackActivityFragment.newInstatnce(artistID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_toptrack, fragment)
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

        if(id == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
