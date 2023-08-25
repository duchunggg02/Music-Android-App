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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    ListView lv_listSong;
    TextView tv_hello;
    DatabaseHelper db;
    SearchView searchView;
    Spinner spinner_Genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            tv_hello.setText("XIN CHÀO, " + username.toUpperCase());
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
    }

    private void initGenre(DatabaseHelper db) {
        db.addGenre("Thể loại");
        db.addGenre("Pop");
        db.addGenre("Ballad");
    }

    private void initSong(DatabaseHelper db) {
        db.addSong("Happy", "duc  HUNG 2", "Pop", R.raw.happy);
        db.addSong("Sao em lại tắt máy", " Phạm Nguyên Ngọc ft. VAnh", "Vpop", R.raw.saoemlaitatmay);
        db.addSong("Lời tâm sự số 3", "Mikelodic", "Rap", R.raw.loitamsuso3);
        db.addSong("Đau để trưởng thành", "Only C", "Vpop", R.raw.daudetruongthanh);
    }
}