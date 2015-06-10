package com.udacity.android.spotify;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class SearchFragment extends Fragment {
    private SpotifyApi api;
    private SpotifyService spotify;
    private ListView mListView;
    private ArtistsPager results;
    private ArtistAdapter artistAdapter;
    private ArrayList<Artist> artists;
    Context context;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        api = new SpotifyApi();
        spotify = api.getService();
        artists = new ArrayList<>();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        artistAdapter = new ArtistAdapter(context, artists);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_artists);
        mListView.setAdapter(artistAdapter);

        return rootView;
    }


    public class SearchArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {
            results = spotify.searchArtists(params[0]);
            return results.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            artistAdapter.clear();
            artistAdapter.addAll(artists);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String queryString) {
                        // Get the query string from searchView
                        new SearchArtistsTask().execute(queryString);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
