package com.udacity.android.spotify.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.android.spotify.R;

public final class DataBinder {
    private DataBinder() {
        // NO-OP
    }

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String imageUrl) {
        Context context = imageView.getContext();
        if (imageUrl != null)
            Picasso.with(context).load(imageUrl).into(imageView);
        else
            imageView.setImageResource(R.drawable.spotify);
    }
}
