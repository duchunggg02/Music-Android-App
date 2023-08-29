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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    ListView lv_favoriteSongs;
    List<Song> favoriteSongsList;
    Button btnLogin;
    TextView tv1, tv2;
    DatabaseHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_favorite, container, false);
        LocaleManager.setLocale(getContext());
        anhXa(view);
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            favoriteSongsList = db.getFavoriteSongs(userId);
            if (favoriteSongsList.isEmpty()) {
                lv_favoriteSongs.setVisibility(View.GONE);
                tv1.setVisibility(View.VISIBLE);
            }

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
        } else {
            lv_favoriteSongs.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }


        return view;
    }
    private void anhXa(View view) {
        lv_favoriteSongs = (ListView) view.findViewById(R.id.lv_favoriteSongs);
        db = new DatabaseHelper(getActivity());
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        btnLogin = (Button) view.findViewById(R.id.login);
    }
}