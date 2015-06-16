package com.udacity.android.spotify;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment {
    private SpotifyApi api;
    private SpotifyService spotify;
    @InjectView(R.id.listview_artists)
    ListView mListView;
    private ArtistAdapter artistAdapter;
    private ArrayList<SpotifyArtist> artists;
    static final String STRING_ARTISTS = "string_artists";
    private String mArtistsString;
    @InjectView(R.id.logo)
    ImageView mImageLogo;
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

        if (savedInstanceState != null) {
            artists.clear();
            artists = savedInstanceState.getParcelableArrayList(STRING_ARTISTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);

        artistAdapter = new ArtistAdapter(context, artists);
        mListView.setAdapter(artistAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyArtist artist = artists.get(position);
                Intent intent = new Intent(context, TopTrackActivity.class);
                intent.putExtra(TopTrackActivity.ARTIST_ID, artist.id);
                intent.putExtra(TopTrackActivity.ARTIST_NAME, artist.name);
                startActivity(intent);
            }
        });
        if (savedInstanceState != null)
            mImageLogo.setVisibility(View.GONE);

        return rootView;
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
                        mArtistsString = queryString;
                        searchArtists();
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

    private void searchArtists() {
        if (!Utility.isNetworkAvailable(context)) {
            Toast.makeText(context, "No Internet, please check your network connection", Toast.LENGTH_SHORT).show();
        } else {
            spotify.searchArtists(mArtistsString, new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showArtists(artistsPager.artists.items);
                        }
                    });
                }

                @Override
                public void failure(final RetrofitError error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // showErrorMessage(error.getMessage());
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void showArtists(List<Artist> items) {
        artistAdapter.clear();
        if (items.size() > 0) {
            mImageLogo.setVisibility(View.GONE);

            String image = null;
            for (Artist artist : items) {
                if (artist.images.size() > 0) {
                    image = artist.images.get(0).url;
                }
                artistAdapter.add(new SpotifyArtist(artist.id, artist.name, image));
            }
        } else {
            Toast.makeText(context, "No artist found. Please refine your search", Toast.LENGTH_SHORT).show();
            if (mImageLogo.getVisibility() == View.GONE)
                mImageLogo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STRING_ARTISTS, artists);
        super.onSaveInstanceState(outState);
    }
}
