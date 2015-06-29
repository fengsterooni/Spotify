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
import com.udacity.android.spotify.models.SpotifyArtist;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ArtistAdapter extends ArrayAdapter<SpotifyArtist> {

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView profileImage;
        @InjectView(R.id.name)
        TextView name;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public ArtistAdapter(Context context, List<SpotifyArtist> artists) {
        super(context, android.R.layout.simple_list_item_1, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SpotifyArtist artist = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(artist.getName());
        viewHolder.profileImage.setImageResource(android.R.color.transparent);

        if (artist.getImage() != null)
            Picasso.with(getContext()).load(artist.getImage()).into(viewHolder.profileImage);
        else
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}