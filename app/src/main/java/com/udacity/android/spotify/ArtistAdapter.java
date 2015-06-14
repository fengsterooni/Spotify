package com.udacity.android.spotify;

import android.content.Context;
import android.content.Intent;
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

public class ArtistAdapter extends ArrayAdapter<SpotifyArtist> {
    Context mContext;
    String mID;

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
        mContext = context;
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

        viewHolder.name.setText(artist.name);
        viewHolder.profileImage.setImageResource(android.R.color.transparent);

        if (artist.image != null)
            Picasso.with(getContext()).load(artist.image).into(viewHolder.profileImage);

        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mID = artist.id;
                getTopTracks();
            }
        });

        viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mID = artist.id;
                getTopTracks();
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    private void getTopTracks() {
        Intent intent = new Intent(mContext, TopTrackActivity.class);
        intent.putExtra("id", mID);
        mContext.startActivity(intent);
    }

}