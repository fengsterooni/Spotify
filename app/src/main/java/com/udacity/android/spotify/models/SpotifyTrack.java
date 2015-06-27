package com.udacity.android.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrack implements Parcelable{
    private String id;
    private String artistName;
    private String trackName;
    private String albumName;
    private String profileImage;
    private String uri;

    public SpotifyTrack(String id, String artistName,
                        String trackName, String albumName, String profileImage, String uri) {
        this.id = id;
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.profileImage = profileImage;
        this.uri = uri;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getId() {
        return id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.artistName);
        dest.writeString(this.trackName);
        dest.writeString(this.albumName);
        dest.writeString(this.profileImage);
        dest.writeString(this.uri);
    }

    protected SpotifyTrack(Parcel in) {
        this.id = in.readString();
        this.artistName = in.readString();
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.profileImage = in.readString();
        this.uri = in.readString();
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
