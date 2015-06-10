package com.udacity.android.spotify;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class TopTrackActivityFragment extends Fragment {
    private SpotifyApi api;
    private SpotifyService spotify;
    private ListView mListView;
    private TrackAdapter trackAdapter;
    private List<Track> tracks;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_track, container, false);
        trackAdapter = new TrackAdapter(context, tracks);
        mListView = (ListView) view.findViewById(R.id.listview_tracks);
        mListView.setAdapter(trackAdapter);

        new TopTrackTask().execute(artist);

        return view;
    }

    public class TopTrackTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {
            Map<String, Object> options = new HashMap<>();
            options.put(spotify.COUNTRY, Locale.getDefault().getCountry());
            tracks = spotify.getArtistTopTrack(params[0], options).tracks;
            return tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            trackAdapter.clear();
            trackAdapter.addAll(tracks);
        }
    }
}
