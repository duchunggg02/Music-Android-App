package com.example.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    TextView tv_songTitle, tv_songArtist;

    public SongAdapter(Context context, List<Song> songList) {
        super(context, R.layout.list_item_song, songList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_song, parent, false);
        }

        Song song = getItem(position);
        tv_songTitle = view.findViewById(R.id.tv_titleSong);
        tv_songTitle.setText(song.getTitle());
        tv_songArtist = view.findViewById(R.id.tv_titleArtist);
        tv_songArtist.setText(song.getArtist());

        return view;
    }
}