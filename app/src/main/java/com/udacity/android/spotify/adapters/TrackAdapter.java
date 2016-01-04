package com.udacity.android.spotify.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.databinding.ListItemTrackBinding;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private List<SpotifyTrack> mTracks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ListItemTrackBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bind(SpotifyTrack track) {
            binding.setTrack(track);
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
        viewHolder.bind(track);
    }


    @Override
    public int getItemCount() {
        return mTracks.size();
    }
}