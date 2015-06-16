package com.udacity.android.spotify;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
    static final String STRING_TRACKS = "string_tracks";
    static final String STRING_ARTIST = "string_artist";
    String artist;
    Context context;

    public TopTrackActivityFragment() {
    }

    public static TopTrackActivityFragment newInstatnce(String artist) {
        TopTrackActivityFragment fragment = new TopTrackActivityFragment();
        Bundle args = new Bundle();
        args.putString("id", artist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracks = new ArrayList<>();
        api = new SpotifyApi();
        spotify = api.getService();
        artist = getArguments().getString("id");
        context = getActivity();

        if (savedInstanceState == null || artist != savedInstanceState.getString(STRING_ARTIST)) {
            searchTopTracks();
        } else {
            tracks.clear();
            tracks = savedInstanceState.getParcelableArrayList(STRING_TRACKS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_track, container, false);
        ButterKnife.inject(this, view);
        trackAdapter = new TrackAdapter(context, tracks);
        mListView.setAdapter(trackAdapter);
        return view;
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
            final Map<String, Object> options = new HashMap<>();
            options.put(spotify.COUNTRY, Locale.getDefault().getCountry());
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
                                }
                            } else {
                                Toast.makeText(context, "No tracks found. Please check other artist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}