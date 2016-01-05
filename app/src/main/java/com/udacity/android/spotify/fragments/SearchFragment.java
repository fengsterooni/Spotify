package com.udacity.android.spotify.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.activities.MainActivity;
import com.udacity.android.spotify.adapters.ArtistAdapter;
import com.udacity.android.spotify.databinding.FragmentSearchBinding;
import com.udacity.android.spotify.models.SpotifyArtist;
import com.udacity.android.spotify.utils.DividerItemDecoration;
import com.udacity.android.spotify.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment implements RecyclerView.OnItemTouchListener {
    private SpotifyApi api;
    private SpotifyService spotify;
    private RecyclerView mRecyclerView;
    private ArtistAdapter artistAdapter;
    private ArrayList<SpotifyArtist> artists;
    private static final String STRING_ARTISTS = "string_artists";
    private String mArtistsString;
    private Context context;
    private GestureDetectorCompat gDetector;

    private FragmentSearchBinding binding;

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
        // View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        // ButterKnife.bind(this, rootView);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        mRecyclerView = binding.listviewArtists;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setVerticalScrollBarEnabled(true);
        mRecyclerView.addOnItemTouchListener(this);

        artistAdapter = new ArtistAdapter(artists);
        mRecyclerView.setAdapter(artistAdapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        gDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = mRecyclerView.getChildPosition(view);
                if (position < 0) return false;
                SpotifyArtist artist = artists.get(position);
                ((OnItemSelected) getActivity()).onItemSelected(artist.getId(), artist.getName());
                return super.onSingleTapConfirmed(e);
            }
        });

        // https://github.com/bingoogolapple/DataBindingNote/blob/master/app/src/main/java/cn/bingoogolapple/databinding/fragment/DemoFragment.java
        return binding.getRoot();
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
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Toast.makeText(context, "No Internet, please check your network connection", Toast.LENGTH_SHORT).show();
            new MaterialDialog.Builder(context)
                    .title(R.string.no_network_title)
                    .content(R.string.no_network_message)
                    .positiveText(R.string.OK)
                    .show();
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
                            // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            new MaterialDialog.Builder(context)
                                    .title(error.getMessage())
                                    .content(error.getMessage())
                                    .positiveText(R.string.OK)
                                    .show();
                        }
                    });
                }
            });
        }
    }

    private void showArtists(List<Artist> items) {
        // artistAdapter.clear();
        artists.clear();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.TOPTRACK_TAG);
        if (fragment != null)
            // ft.hide(fragment).commit();
            ft.remove(fragment).commit();

        if (items.size() > 0) {
            String image = null;
            for (Artist artist : items) {
                if (artist.images.size() > 0) {
                    image = artist.images.get(0).url;
                }
                // artistAdapter.add(new SpotifyArtist(artist.id, artist.name, image));
                artists.add(new SpotifyArtist(artist.id, artist.name, image));
                image = null;
            }
        } else {
            // Toast.makeText(context, "No artist found. Please refine your search", Toast.LENGTH_SHORT).show();
            new MaterialDialog.Builder(context)
                    .title(R.string.no_artist_title)
                    .content(R.string.no_artist_message)
                    .positiveText(R.string.OK)
                    .show();
        }

        artistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STRING_ARTISTS, artists);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemSelected {
        void onItemSelected(String artistID, String artistName);
    }
}
