package com.udacity.android.spotify;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class TopTrackActivityFragment extends Fragment {
    private SpotifyApi api;
    private SpotifyService spotify;
    @InjectView(R.id.listview_tracks)ListView mListView;
    private TrackAdapter trackAdapter;
    private ArrayList<Track> tracks;
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
            new TopTrackTask().execute(artist);
        } else {
            tracks.clear();
            tracks = (ArrayList<Track>) savedInstanceState.getSerializable(STRING_TRACKS);
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

    public class TopTrackTask extends AsyncTask<String, Void, List<Track>> {
        @Override
        protected List<Track> doInBackground(String... params) {
            Map<String, Object> options = new HashMap<>();
            options.put(spotify.COUNTRY, Locale.getDefault().getCountry());
            return spotify.getArtistTopTrack(params[0], options).tracks;
        }

        @Override
        protected void onPostExecute(List<Track> allTracks) {
            trackAdapter.clear();
            if (allTracks.size() > 0) {
                tracks.addAll(allTracks);
            }
            else
                Toast.makeText(context, "No tracks found. Please check other artist.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STRING_ARTIST, artist);
        outState.putSerializable(STRING_TRACKS, tracks);
        super.onSaveInstanceState(outState);
    }
}
