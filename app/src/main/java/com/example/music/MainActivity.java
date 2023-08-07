package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lv_listSong, lv_listGenre;
    Button btn_DangXuat, btn_listFavorite;
    TextView tv_hello;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();

        // db.deleteAllSongs();
        // this.deleteDatabase(DatabaseHelper.DATABASE_NAME);

        db.addGenre("Pop");
        db.addGenre("Ballad");

        db.addSong("Happy", "duc  HUNG 2", "Pop", R.raw.happy);
        db.addSong("Sao em lại tắt máy", "duc  HUNG 3", "Ballad", R.raw.saoemlaitatmay);
        db.addSong("bgm", "duc  HUNG 1", "Pop", R.raw.bgm);
        db.addSong("Lời tâm sự số 3", "duc  HUNG 4", "Meo", R.raw.loitamsuso3);
        db.addSong("Lời tâm sự số 4", "duc  HUNG 4", "House", R.raw.loitamsuso3);

        List<Song> songList = db.getAllSongs();
        List<Genre> genreList = db.getAllGenres();

        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(this, R.layout.list_item_song, R.id.tv_titleSong,
                songList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Song song = getItem(position);
                TextView textViewSongTitle = view.findViewById(R.id.tv_titleSong);
                textViewSongTitle.setText(song.getTitle());
                return view;
            }
        };
        lv_listSong.setAdapter(adapter);

        ArrayAdapter<Genre> adapterGenre = new ArrayAdapter<Genre>(this, R.layout.list_item_genre, R.id.tv_titleGenre,
                genreList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Genre genre = getItem(position);
                TextView textViewGenreTitle = view.findViewById(R.id.tv_titleGenre);
                textViewGenreTitle.setText(genre.getName());
                return view;
            }
        };
        lv_listGenre.setAdapter(adapterGenre);

        lv_listSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Song selectedSong = songList.get(position);

                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);

                intent.putParcelableArrayListExtra("songList", (ArrayList<? extends Parcelable>) songList);
                intent.putExtra("position", position);

                startActivity(intent);

            }
        });

        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        tv_hello.setText("XIN CHÀO " + username);

        btn_DangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        btn_listFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteSongs.class);
                startActivity(intent);
            }
        });
    }

    private void anhXa() {
        lv_listSong = findViewById(R.id.lv_listSong);
        lv_listGenre = findViewById(R.id.lv_listGenre);
        btn_DangXuat = findViewById(R.id.btn_DangXuat);
        btn_listFavorite = findViewById(R.id.btn_listFavorite);
        tv_hello = findViewById(R.id.tv_hello);
        db = new DatabaseHelper(MainActivity.this);
    }

    private void logOut() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}