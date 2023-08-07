package com.example.music;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Song implements Parcelable {

    private int id;
    private String title;
    private String artist;
    private Genre genre;
    private int path;

    public Song() {
    }

    public Song(int id, String title, String artist, Genre genre, int path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return title;
    }

    // Parcelable
    protected Song(Parcel in) {
        id = in.readInt();
        title = in.readString();
        artist = in.readString();
        genre = in.readParcelable(Genre.class.getClassLoader());
        path = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeParcelable(genre, flags);
        dest.writeInt(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

}
