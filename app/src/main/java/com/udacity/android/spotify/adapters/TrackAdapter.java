package com.udacity.android.spotify.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private List<SpotifyTrack> mTracks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static Context context;
        @Bind(R.id.image)ImageView profileImage;
        @Bind(R.id.albumName)TextView albumName;
        @Bind(R.id.trackName)TextView trackName;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            ButterKnife.bind(this, view);
        }

        public void setAlbumName(CharSequence text) {
            albumName.setText(text);
        }

        public void setTrackName(CharSequence text) {
            trackName.setText(text);
        }

        public void setProfileImage(String imageUrl) {
            Picasso.with(context).load(imageUrl).into(profileImage);
        }

        public void setImageResource(int resourceID) {
            profileImage.setImageResource(resourceID);
        }
    }

    public TrackAdapter(List<SpotifyTrack> tracks) {
        this.mTracks = tracks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final SpotifyTrack track = mTracks.get(position);

        viewHolder.setTrackName(track.getTrackName());
        viewHolder.setAlbumName(track.getAlbumName());

        String imageUrl = track.getProfileImage();
        if (imageUrl != null)
            viewHolder.setProfileImage(imageUrl);
        else
            viewHolder.setImageResource(R.drawable.spotify);

    }


    @Override
    public int getItemCount() {
        return mTracks.size();
    }
}