package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class FavoriteSongs extends AppCompatActivity {

    ListView lv_favoriteSongs;
    FavoriteSongsAdapter adapter;
    List<Song> favoriteSongsList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);

        anhXa();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        favoriteSongsList = db.getFavoriteSongs(userId);

        // Khởi tạo và thiết lập adapter cho ListView
        adapter = new FavoriteSongsAdapter(this, favoriteSongsList);
        lv_favoriteSongs.setAdapter(adapter);
    }

    private void anhXa() {
        lv_favoriteSongs = (ListView) findViewById(R.id.lv_favoriteSongs);
        db = new DatabaseHelper(FavoriteSongs.this);
    }
}