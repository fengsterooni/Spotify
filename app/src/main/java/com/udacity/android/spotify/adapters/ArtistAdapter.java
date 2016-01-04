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
import com.udacity.android.spotify.models.SpotifyArtist;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private List<SpotifyArtist> mArtists;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static Context context;
        @Bind(R.id.image)
        ImageView profileImage;
        @Bind(R.id.name)
        TextView name;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            ButterKnife.bind(this, view);
        }

        public void setName(CharSequence text) {
            name.setText(text);
        }

        public void setProfileImage(String imageUrl) {
            Picasso.with(context).load(imageUrl).into(profileImage);
        }

        public void setImageResource(int resourceID) {
            profileImage.setImageResource(resourceID);
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

        viewHolder.setName(artist.getName());
        viewHolder.setImageResource(android.R.color.transparent);

        if (artist.getImage() != null)
            viewHolder.setProfileImage(artist.getImage());
        else
            viewHolder.setImageResource(R.drawable.spotify);
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }
}