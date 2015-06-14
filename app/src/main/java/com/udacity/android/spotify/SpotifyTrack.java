package com.udacity.android.spotify;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrack implements Parcelable{
    String id;
    String trackName;
    String albumName;
    String profileImage;

    public SpotifyTrack(String id, String trackName, String albumName, String profileImage) {
        this.id = id;
        this.trackName = trackName;
        this.albumName = albumName;
        this.profileImage = profileImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.trackName);
        dest.writeString(this.albumName);
        dest.writeString(this.profileImage);
    }

    protected SpotifyTrack(Parcel in) {
        this.id = in.readString();
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.profileImage = in.readString();
    }

    public static final Creator<SpotifyTrack> CREATOR = new Creator<SpotifyTrack>() {
        public SpotifyTrack createFromParcel(Parcel source) {
            return new SpotifyTrack(source);
        }

        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };
}
