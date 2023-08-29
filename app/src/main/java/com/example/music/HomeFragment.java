package com.example.music;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    ListView lv_listSong;
    TextView tv_hello;
    DatabaseHelper db;
    SearchView searchView;
    Spinner spinner_Genre;
    boolean nightMode;
    SharedPreferences p;
    private Switch switchDarkMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocaleManager.setLocale(getContext());
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        anhXa(view);
//        Clear database
//        db.deleteAllSongs();
//        getActivity().deleteDatabase(DatabaseHelper.DATABASE_NAME);

        initGenre(db);
        initSong(db);

        List<Genre> genreList = db.getAllGenres();
        List<Song> songList = db.getAllSongs();

        SharedPreferences preferences = getActivity().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        if (!username.isEmpty()) {
            tv_hello.setText(getString(R.string.tv_hello) + ", " + username);
        }

        SongAdapter adapter = new SongAdapter(getActivity(), songList);
        lv_listSong.setAdapter(adapter);

        ArrayAdapter<Genre> adapterGenre = new ArrayAdapter<Genre>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                genreList);
        spinner_Genre.setAdapter(adapterGenre);

        spinner_Genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Genre selectedGenre = (Genre) parent.getItemAtPosition(position);

                if (selectedGenre.getName().equals("Thể loại")) {
                    SongAdapter adapter = new SongAdapter(getActivity(), songList);
                    lv_listSong.setAdapter(adapter);
                } else {
                    List<Song> songListByGenre = db.getSongsByGenre(selectedGenre.getId());
                    SongAdapter adapter = new SongAdapter(getActivity(), songListByGenre);
                    lv_listSong.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lv_listSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                stopCurrentSong();

                Intent intent = new Intent(getActivity(), PlayMusicActivity.class);

                intent.putParcelableArrayListExtra("songList", (ArrayList<? extends Parcelable>) songList);
                intent.putExtra("position", position);

                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                List<Song> songFilter = new ArrayList<>();
//                for (Song song: songList){
//                    if(song.getTitle().toUpperCase().contains(newText.toUpperCase())){
//                        songFilter.add(song);
//                    }
//                    SongAdapter adapter = new SongAdapter(getActivity(), songFilter);
//                    lv_listSong.setAdapter(adapter);
//                }
//                return true;
                String selectedGenre = spinner_Genre.getSelectedItem().toString(); // Lấy giá trị được chọn từ Spinner
                List<Song> songFilter = new ArrayList<>();

                for (Song song : songList) {
                    int songTitleIndex =   song.getTitle().toUpperCase().indexOf(newText.trim().toUpperCase());
                    int songArtistIndex = song.getArtist().toUpperCase().indexOf(newText.trim().toUpperCase());

                    boolean songNameArtist = songTitleIndex != -1 || songArtistIndex != -1;
                    if (songNameArtist
                            && (selectedGenre.equals("Thể loại") || song.getGenre().getName().equals(selectedGenre))) {
                        songFilter.add(song);
                    }
                }

                SongAdapter adapter = new SongAdapter(getActivity(), songFilter);
                lv_listSong.setAdapter(adapter);

                return true;
            }
        });

        p = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = p.getBoolean("night", false);
        AppCompatDelegate.setDefaultNightMode(nightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        switchDarkMode.setChecked(nightMode);
        switchDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = p.edit();
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    e.putBoolean("night", false);
                    Toast.makeText(getContext(), "Light mode", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    e.putBoolean("night", true);
                    Toast.makeText(getContext(), "Night mode", Toast.LENGTH_SHORT).show();
                }
                e.apply();

                // Cập nhật lại giá trị của nightMode
                nightMode = !nightMode;
                switchDarkMode.setChecked(nightMode);
            }
        });
        return view;
    }

    private void stopCurrentSong() {
        Intent stopIntent = new Intent("stop_song");
        getActivity().sendBroadcast(stopIntent);
    }

    private void anhXa(View view) {
        lv_listSong = (ListView) view.findViewById(R.id.lv_listSong);
        tv_hello = (TextView) view.findViewById(R.id.tv_hello);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        db = new DatabaseHelper(getActivity());
        spinner_Genre = (Spinner) view.findViewById(R.id.spinner_Genre);
        switchDarkMode = (Switch) view.findViewById(R.id.switchDarkMode);
    }

    private void initGenre(DatabaseHelper db) {
        db.addGenre("Thể loại");
        db.addGenre("Pop");
        db.addGenre("Ballad");
    }

    private void initSong(DatabaseHelper db) {
        db.addSong("À Lôi", "Double2T", "Rap", R.raw.aloi);
        db.addSong("Sao em lại tắt máy", "Phạm Nguyên Ngọc ft. VAnh", "Rap", R.raw.saoemlaitatmay);
        db.addSong("Lời tâm sự số 3", "Mikelodic", "Rap", R.raw.loitamsuso3);
        db.addSong("Đau để trưởng thành", "Only C", "Pop", R.raw.daudetruongthanh);
        db.addSong("Xóa tên anh đi", "Jack", "Pop", R.raw.xoatenanhdi);
        db.addSong("Sau này chúng ta giàu", "Khắc Việt", "Vpop", R.raw.saunaychungtagiau);
        db.addSong("Ngày mai người ta lấy chồng", "Thành Đạt", "Rap", R.raw.ngaymainguoitalaychong);
        db.addSong("Hoa cỏ lau", "Phong Max", "Vpop", R.raw.hoacolau);
        db.addSong("Em là ai", "Keyo", "Rap", R.raw.emlaai);
        db.addSong("Có ai hẹn hò cùng em chưa", "Quân AP", "Vpop", R.raw.coaihenhocungemchua);
    }
}