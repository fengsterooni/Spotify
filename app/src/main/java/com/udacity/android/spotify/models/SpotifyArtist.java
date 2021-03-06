package com.udacity.android.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyArtist implements Parcelable{
    private String id;
    private String name;
    private String image;

    public SpotifyArtist(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
    }

    protected SpotifyArtist(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
    }

    public static final Creator<SpotifyArtist> CREATOR = new Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source);
        }

        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };
}
