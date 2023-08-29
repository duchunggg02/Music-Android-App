package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.Manifest;


public class PlayMusicActivity extends AppCompatActivity {

    Button prev, play, favorite, next, btn_share;
    TextView title, start, end;
    SeekBar sb;
    ImageView img;

    ArrayList<Song> a;
    MediaPlayer mp;
    Animation anim;
    int pos = 0;

    private boolean isPlaying, isAnimationRunning = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
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
        btn_share = (Button) findViewById(R.id.btn_share);

        DatabaseHelper db = new DatabaseHelper(PlayMusicActivity.this);
        SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        Integer userId = preferences.getInt("userId", -1);


        registerReceiver(stopSongReceiver, new IntentFilter("stop_song"));



        Intent intent = getIntent();
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);


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
        updateFavoriteButtonState(db, userId);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song selectedSong = a.get(pos);

                if (userId != -1) {
                    boolean isFavorite = db.isFavorite(userId, selectedSong.getId());
                    if (isFavorite) {
                        db.removeFavorite(userId, selectedSong.getId());
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_24, 0, 0, 0);
                        Toast.makeText(PlayMusicActivity.this, getString(R.string.deleteFavorite), Toast.LENGTH_SHORT).show();
                    } else {
                        db.addFavorite(userId, selectedSong.getId());
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_24_red, 0, 0, 0);
                        Toast.makeText(PlayMusicActivity.this, getString(R.string.addFavorite), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlayMusicActivity.this, getString(R.string.plsSignin), Toast.LENGTH_SHORT).show();
                }

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.pause();
                    img.clearAnimation();
                    play.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_arrow_24,0,0,0);
                } else {
                    mp.start();
                    img.startAnimation(anim);
                    play.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_24,0,0,0);
                }
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
                updateFavoriteButtonState(db, userId);
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
                updateFavoriteButtonState(db, userId);
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

        
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = a.get(pos);
                shareSong(song);
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
                if (mp != null) {
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

                }



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
        img.setAnimation(anim);
        mp.start();
    }

    private void playSongAtPosition(int position) {
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp = MediaPlayer.create(PlayMusicActivity.this, a.get(position).getPath());
        title.setText(a.get(position).getTitle());
        img.clearAnimation();
        img.startAnimation(anim);
        play.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_pause_24, 0, 0, 0);
        mp.start();
        setTimeEnd();
        setTimeStart();
    }

    private void stopMusic() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
        isPlaying = false;
    }

    private BroadcastReceiver stopSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopMusic();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        unregisterReceiver(stopSongReceiver);
    }
    private File getTempFile(Context context, String fileName) {
        File externalFilesDir = context.getExternalFilesDir(null);
        File tempDir = new File(externalFilesDir, "temp_music");

        if (!tempDir.exists()) {
            tempDir.mkdir();
    }

    return new File(tempDir, fileName);
}


    private void shareSong(Song song) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");

        Resources res = getResources();
        int musicResourceId = res.getIdentifier(String.valueOf(song.getPath()), "raw", getPackageName());

        if (musicResourceId != 0) {
            // Sao chép tệp nhạc vào thư mục tạm thời
            String tempFileName = song.getPath() + ".mp3";
            File tempFile = getTempFile(this, tempFileName);
            try (InputStream inputStream = getResources().openRawResource(musicResourceId);
                 OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share via"));
        } else {
            Toast.makeText(this, "Song not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteButtonState(DatabaseHelper db,int userId) {
        Song selectedSong = a.get(pos);
        boolean isFavorite = db.isFavorite(userId, selectedSong.getId());
        if (isFavorite) {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_24_red, 0, 0, 0);
        } else {
            favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_24, 0, 0, 0);
        }
    }


}
