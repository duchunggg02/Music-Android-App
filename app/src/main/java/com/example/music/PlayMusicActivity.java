package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayMusicActivity extends AppCompatActivity {

    Button prev, play, favorite, next;
    TextView title, start, end;
    SeekBar sb;
    ImageView img;

    ArrayList<Song> a;
    MediaPlayer mp;
    Animation anim;
    int pos = 0;

    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        prev = (Button) findViewById(R.id.btnPrev);
        play = (Button) findViewById(R.id.btnPlay);
        favorite = (Button) findViewById(R.id.btnFavorite);
        next = (Button) findViewById(R.id.btnNext);
        title = (TextView) findViewById(R.id.tv_SongName);
        start = (TextView) findViewById(R.id.tvStart);
        end = (TextView) findViewById(R.id.tvEnd);
        sb = (SeekBar) findViewById(R.id.seekBar);
        img = (ImageView) findViewById(R.id.imageView);

        DatabaseHelper db = new DatabaseHelper(PlayMusicActivity.this);
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        Integer userId = preferences.getInt("userId", -1);



        // anim = AnimationUtils.loadAnimation(this, R.anim.rolate);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("songList") && intent.hasExtra("position")) {


            ArrayList<Song> songList = intent.getParcelableArrayListExtra("songList");
            int currentPosition = intent.getIntExtra("position", 0);
            playMusic(songList.get(currentPosition));
            title.setText(songList.get(currentPosition).getTitle());
            Genre genre = songList.get(currentPosition).getGenre();
            a = songList; // Gán danh sách bài hát
            pos = currentPosition; // Gán vị trí hiện tại
            setTimeEnd();
            setTimeStart();
        }

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song selectedSong = a.get(pos);

                if (userId != -1) {
                    boolean isFavorite = db.isFavorite(userId, selectedSong.getId());

                    if (isFavorite) {
                        db.removeFavorite(userId, selectedSong.getId());
                    } else {
                        db.addFavorite(userId, selectedSong.getId());
                    }

                    if (isFavorite) {
                        Toast.makeText(PlayMusicActivity.this, "Đã xóa khỏi Yêu Thích", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlayMusicActivity.this, "Đã thêm vào Yêu Thích", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    favorite.setVisibility(View.GONE);
                }
                // Kiểm tra xem bài hát đã yêu thích chưa

            }
        });

        //
        // mp = MediaPlayer.create(PlayMusicActivity.this, a.get(pos).getPath());
        // title.setText(a.get(pos).getTitle());
        //

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.pause();
                    // play.setImageResource(R.drawable.);
                } else {
                    mp.start();
                    // play.setImageResource(R.drawable.);
                }
                // setTimeEnd();
                // setTimeStart();
                // img.startAnimation(anim);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos--;
                if (pos < 0) {
                    pos = a.size() - 1;
                }
                playSongAtPosition(pos);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos++;
                if (pos > a.size() - 1) {
                    pos = 0;
                }
                playSongAtPosition(pos);
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(sb.getProgress());
            }
        });

    }

    private void setTimeEnd() {
        SimpleDateFormat fm = new SimpleDateFormat("mm:ss");
        end.setText(fm.format(mp.getDuration()));
        sb.setMax(mp.getDuration());
    }

    private void setTimeStart() {
        Handler hl = new Handler();
        hl.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat fm = new SimpleDateFormat("mm:ss");
                start.setText(fm.format(mp.getCurrentPosition()));
                sb.setProgress(mp.getCurrentPosition());

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        pos++;
                        if (pos > a.size() - 1) {
                            pos = 0;
                        }
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        mp = MediaPlayer.create(PlayMusicActivity.this, a.get(pos).getPath());
                        title.setText(a.get(pos).getTitle());
                        mp.start();
                        setTimeEnd();
                        setTimeStart();
                    }
                });

                hl.postDelayed(this, 500);
            }
        }, 100);
    }

    private void playMusic(Song song) {
        if (isPlaying) {
            stopMusic();
        }
        isPlaying = true;
        mp = MediaPlayer.create(this, song.getPath());

        mp.start();
    }

    private void playSongAtPosition(int position) {
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp = MediaPlayer.create(PlayMusicActivity.this, a.get(position).getPath());
        title.setText(a.get(position).getTitle());
        mp.start();
        setTimeEnd();
        setTimeStart();
    }

    private void stopMusic() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        isPlaying = false;
    }

}
