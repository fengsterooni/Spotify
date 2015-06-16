package com.udacity.android.spotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

        viewHolder.albumName.setText(track.albumName);
        viewHolder.trackName.setText(track.trackName);
        viewHolder.profileImage.setImageResource(android.R.color.transparent);

        if (track.profileImage != null)
            Picasso.with(getContext()).load(track.profileImage).into(viewHolder.profileImage);

        return convertView;
    }
}