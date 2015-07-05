package com.udacity.android.spotify.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.SpotifyApplication;
import com.udacity.android.spotify.activities.MainActivity;
import com.udacity.android.spotify.activities.PlayerActivity;
import com.udacity.android.spotify.activities.TopTrackActivity;
import com.udacity.android.spotify.adapters.TrackAdapter;
import com.udacity.android.spotify.models.SpotifyTrack;
import com.udacity.android.spotify.utils.ImageUtils;
import com.udacity.android.spotify.utils.NetworkUtils;

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
    private ArrayList<SpotifyTrack> mTracks;
    private int mPosition;

    static ArrayList<SpotifyTrack> cTracks;
    static int cPosition;

    static final String STRING_TRACKS = "STRING_TRACKS";
    static final String STRING_ARTIST = "STRING_ARTIST";
    public static final String PLAYER_TAG = "PLAYER";

    private final String LOG_TAG = TopTrackActivityFragment.class.getSimpleName();

    String artistID;
    Context context;

    public TopTrackActivityFragment() {
    }

    public static TopTrackActivityFragment newInstatnce(String artistID) {
        TopTrackActivityFragment fragment = new TopTrackActivityFragment();
        Bundle args = new Bundle();
        if (artistID != null) {
            args.putString(TopTrackActivity.ARTIST_ID, artistID);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        mTracks = new ArrayList<>();
        api = new SpotifyApi();
        spotify = api.getService();

        Bundle args = getArguments();
        if (args != null) {
            artistID = args.getString(TopTrackActivity.ARTIST_ID);

            if (savedInstanceState == null) {
                searchTopTracks();
            } else {
                mTracks.clear();
                mTracks = savedInstanceState.getParcelableArrayList(STRING_TRACKS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_track, container, false);
        ButterKnife.inject(this, view);
        trackAdapter = new TrackAdapter(context, mTracks);
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

    public void popupPlayer(ArrayList<SpotifyTrack> tracks, int position) {
        mTracks = tracks;
        mPosition = position;
        popupPlayer();
    }

    public void popupPlayer() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PlayerDialog playerDialog = PlayerDialog.newInstance(mTracks, mPosition);

        if (MainActivity.ismTwoPane())
            playerDialog.show(fm, PLAYER_TAG);
        else {
            /*
            fm.beginTransaction()
                    .replace(R.id.fragment_toptrack, playerDialog, PLAYER_TAG)
                    .addToBackStack(null)
                    .commit();
                    */
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra(PlayerDialog.TOP_TRACKS, mTracks);
            intent.putExtra(PlayerDialog.TRACK_POSITION, mPosition);
            startActivity(intent);
        }
    }

    public void popupCurrent() {
        cTracks = SpotifyApplication.getAppTracks();
        cPosition = SpotifyApplication.getAppPosition();

        if (cTracks != null && cTracks.size() > 0)
            popupPlayer(cTracks, cPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STRING_ARTIST, artistID);
        outState.putParcelableArrayList(STRING_TRACKS, mTracks);
        super.onSaveInstanceState(outState);
    }

    private void searchTopTracks() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Toast.makeText(context, "No Internet, please check your network connection", Toast.LENGTH_SHORT).show();
            new MaterialDialog.Builder(context)
                    .title(R.string.no_network_title)
                    .content(R.string.no_network_message)
                    .positiveText(R.string.OK)
                    .show();
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String code = pref.getString(getString(R.string.pref_list_country_code_key), Locale.getDefault().getCountry());
            final Map<String, Object> options = new HashMap<>();
            options.put(spotify.COUNTRY, code);
            spotify.getArtistTopTrack(artistID, options, new Callback<Tracks>() {
                @Override
                public void success(final Tracks tracks, Response response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            trackAdapter.clear();
                            if (tracks.tracks.size() > 0) {
                                String image = null;
                                for (Track track : tracks.tracks) {
                                    if (track.album.images.size() > 0) {
                                        image = track.album.images.get(0).url;
                                        ImageUtils.getFromImageCache(image);
                                    }

                                    SpotifyTrack newTrack = new SpotifyTrack(
                                            track.id,
                                            artistID,
                                            track.artists.get(0).name,
                                            track.name,
                                            track.album.name,
                                            image,
                                            track.preview_url);

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
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            // Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
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
}
