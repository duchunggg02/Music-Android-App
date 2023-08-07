package com.example.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class FavoriteSongsAdapter extends ArrayAdapter<Song> {
    public FavoriteSongsAdapter(Context context, List<Song> favoriteSongsList) {
        super(context, 0, favoriteSongsList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_song_favorite, parent, false);
        }

        Song song = getItem(position);
        TextView tvSongTitle = convertView.findViewById(R.id.tv_titleSongFavorite);
        tvSongTitle.setText(song.getTitle());

        return convertView;
    }
}
