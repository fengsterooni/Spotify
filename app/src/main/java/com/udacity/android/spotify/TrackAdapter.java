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

import kaaes.spotify.webapi.android.models.Track;

public class TrackAdapter extends ArrayAdapter<Track> {
    Context mContext;

    static class ViewHolder {
        ImageView profileImage;
        TextView albumName;
        TextView trackName;

        ViewHolder(View view) {
            profileImage = (ImageView) view.findViewById(R.id.image);
            albumName = (TextView) view.findViewById(R.id.albumName);
            trackName = (TextView) view.findViewById(R.id.trackName);
        }
    }

    public TrackAdapter(Context context, List<Track> tracks) {
        super(context, android.R.layout.simple_list_item_1, tracks);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Track track = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.albumName.setText(track.album.name);
        viewHolder.trackName.setText(track.name);
        viewHolder.profileImage.setImageResource(android.R.color.transparent);
        //viewHolder.profileImage.setTag(track.album.images.get(0).url);
        Picasso.with(getContext()).load(track.album.images.get(0).url).into(viewHolder.profileImage);

        return convertView;
    }
}