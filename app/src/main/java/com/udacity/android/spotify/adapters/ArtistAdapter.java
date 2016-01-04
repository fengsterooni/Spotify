package com.udacity.android.spotify.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.android.spotify.R;
import com.udacity.android.spotify.databinding.ListItemArtistBinding;
import com.udacity.android.spotify.models.SpotifyArtist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private List<SpotifyArtist> mArtists;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ListItemArtistBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bind(SpotifyArtist artist) {
            binding.setArtist(artist);
        }
    }

    public ArtistAdapter(List<SpotifyArtist> artists) {
        this.mArtists = artists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final SpotifyArtist artist = mArtists.get(position);
        viewHolder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }
}