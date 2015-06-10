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

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    Context mContext;
    String mID;

    static class ViewHolder {
        ImageView profileImage;
        TextView name;

        ViewHolder(View view) {
            profileImage = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public ArtistAdapter(Context context, List<Artist> artists) {
        super(context, android.R.layout.simple_list_item_1, artists);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Artist artist = getItem(position);
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
        //viewHolder.profileImage.setTag(artist);
        Picasso.with(getContext()).load(artist.images.get(0).url).into(viewHolder.profileImage);

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