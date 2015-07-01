package com.udacity.android.spotify.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.activities.MainActivity;
import com.udacity.android.spotify.activities.TopTrackActivity;
import com.udacity.android.spotify.adapters.TrackAdapter;
import com.udacity.android.spotify.models.SpotifyTrack;
import com.udacity.android.spotify.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopTrackActivityFragment extends Fragment {
    private SpotifyApi api;
    private SpotifyService spotify;
    @InjectView(R.id.listview_tracks)
    ListView mListView;
    private TrackAdapter trackAdapter;
    private ArrayList<SpotifyTrack> tracks;
    private int mPosition;
    static final String STRING_TRACKS = "STRING_TRACKS";
    static final String STRING_ARTIST = "STRING_ARTIST";
    String artist;
    Context context;

    public TopTrackActivityFragment() {
    }

    public static TopTrackActivityFragment newInstatnce(String artist) {
        TopTrackActivityFragment fragment = new TopTrackActivityFragment();
        Bundle args = new Bundle();
        if (artist != null) {
            args.putString(TopTrackActivity.ARTIST_ID, artist);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        tracks = new ArrayList<>();
        api = new SpotifyApi();
        spotify = api.getService();

        Bundle args = getArguments();
        if (args != null) {
            artist = args.getString(TopTrackActivity.ARTIST_ID);

            if (savedInstanceState == null) {
                searchTopTracks();
            } else {
                tracks.clear();
                tracks = savedInstanceState.getParcelableArrayList(STRING_TRACKS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_track, container, false);
        ButterKnife.inject(this, view);
        trackAdapter = new TrackAdapter(context, tracks);
        mListView.setAdapter(trackAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                popupPlayer();
            }
        });

        return view;
    }

    public void popupPlayer() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PlayerDialog playerDialog = PlayerDialog.newInstance(tracks, mPosition);

        if (MainActivity.ismTwoPane())
            playerDialog.show(fm, "Player");
        else {
                    /*
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(PlayerDialog.TOP_TRACKS, tracks);
                    intent.putExtra(PlayerDialog.TRACK_POSITION, position);
                    startActivity(intent);
                    */
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_toptrack, playerDialog, "player")
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STRING_ARTIST, artist);
        outState.putParcelableArrayList(STRING_TRACKS, tracks);
        super.onSaveInstanceState(outState);
    }

    private void searchTopTracks() {
        if (!Utility.isNetworkAvailable(context)) {
            Toast.makeText(context, "No Internet, please check your network connection", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String code = pref.getString("country", Locale.getDefault().getCountry());
            // Log.i("INFO", "GOT THE CODE HERE: " + code);
            final Map<String, Object> options = new HashMap<>();
            options.put(spotify.COUNTRY, code);
            spotify.getArtistTopTrack(artist, options, new Callback<Tracks>() {
                @Override
                public void success(final Tracks tracks, Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            trackAdapter.clear();
                            if (tracks.tracks.size() > 0) {
                                String image = null;
                                for (Track track : tracks.tracks) {
                                    if (track.album.images.size() > 0)
                                        image = track.album.images.get(0).url;

                                    SpotifyTrack newTrack = new SpotifyTrack(track.id, track.artists.get(0).name,
                                            track.name, track.album.name, image, track.preview_url);

                                    trackAdapter.add(newTrack);
                                    image = null;
                                }
                            } else {
                                Toast.makeText(context, "No tracks found. Please check other artist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void failure(final RetrofitError error) {
                    // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }
}
