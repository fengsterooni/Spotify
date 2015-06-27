package com.udacity.android.spotify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.spotify.R;
import com.udacity.android.spotify.models.SpotifyTrack;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrackAdapter extends ArrayAdapter<SpotifyTrack> {

    static class ViewHolder {
        @InjectView(R.id.image)ImageView profileImage;
        @InjectView(R.id.albumName)TextView albumName;
        @InjectView(R.id.trackName)TextView trackName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public TrackAdapter(Context context, List<SpotifyTrack> tracks) {
        super(context, android.R.layout.simple_list_item_1, tracks);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SpotifyTrack track = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.albumName.setText(track.getAlbumName());
        viewHolder.trackName.setText(track.getTrackName());
        viewHolder.profileImage.setImageResource(android.R.color.transparent);

        if (track.getProfileImage() != null)
            Picasso.with(getContext()).load(track.getProfileImage()).into(viewHolder.profileImage);

        return convertView;
    }
}