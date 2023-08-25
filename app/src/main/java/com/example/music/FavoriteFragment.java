package com.example.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    ListView lv_favoriteSongs;
    List<Song> favoriteSongsList;
    DatabaseHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favorite, container, false);

        anhXa(view);
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        favoriteSongsList = db.getFavoriteSongs(userId);

        SongAdapter adapter = new SongAdapter(getActivity(), favoriteSongsList);
        lv_favoriteSongs.setAdapter(adapter);


        lv_favoriteSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PlayMusicActivity.class);

                intent.putParcelableArrayListExtra("songList", (ArrayList<? extends Parcelable>) favoriteSongsList);
                intent.putExtra("position", position);


                startActivity(intent);
            }
        });
        return view;
    }
    private void anhXa(View view) {
        lv_favoriteSongs = (ListView) view.findViewById(R.id.lv_favoriteSongs);
        db = new DatabaseHelper(getActivity());
    }
}