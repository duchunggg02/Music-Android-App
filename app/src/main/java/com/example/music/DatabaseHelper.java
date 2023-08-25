package com.example.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "music.db";
    private static final int DATABASE_VERSION = 1;
    // Bảng user
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    // Bảng song
    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_SONG_ID = "id";
    private static final String COLUMN_SONG_TITLE = "title";
    private static final String COLUMN_SONG_ARTIST = "artist";
    private static final String COLUMN_SONG_GENRE = "genre";
    private static final String COLUMN_SONG_PATH = "path";

    // Bảng thể loại nhạc
    private static final String TABLE_GENRES = "genres";
    private static final String COLUMN_GENRE_ID = "id";
    private static final String COLUMN_GENRE_NAME = "name";

    // Bảng yêu thích

    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_FAVORITE_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FAVORITE_SONG_ID = "song_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableQuery);

        String createSongsTableQuery = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SONG_TITLE + " TEXT,"
                + COLUMN_SONG_ARTIST + " TEXT,"
                + COLUMN_SONG_GENRE + " INTEGER,"
                + COLUMN_SONG_PATH + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_SONG_GENRE + ") REFERENCES " + TABLE_GENRES + "(" + COLUMN_GENRE_ID + "))";
        db.execSQL(createSongsTableQuery);

        String createGenresTableQuery = "CREATE TABLE " + TABLE_GENRES + "("
                + COLUMN_GENRE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GENRE_NAME + " TEXT)";
        db.execSQL(createGenresTableQuery);

        // Tạo bảng Favorites
        String createFavoriteTableQuery = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_FAVORITE_SONG_ID + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_USER_ID + "),"
                + " FOREIGN KEY (" + COLUMN_FAVORITE_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + COLUMN_SONG_ID + "))";
        db.execSQL(createFavoriteTableQuery);
    }

    public int getUserId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Giá trị mặc định khi không tìm thấy

        String[] projection = { "id" }; // Tên cột chứa ID của người dùng
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }

        cursor.close();
        db.close();

        return userId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQueryUsers = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQueryUsers);

        String dropTableQuerySongs = "DROP TABLE IF EXISTS " + TABLE_SONGS;
        db.execSQL(dropTableQuerySongs);

        onCreate(db);
    }

    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return newRowId;
    }

    public boolean isUserValid(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { COLUMN_ID };
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        boolean isValid = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isValid;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );

        boolean usernameExists = cursor.getCount() > 0;
        cursor.close();
        return usernameExists;
    }

    public long addSong(String title, String artist, String genre, int path) {

        if (isSongExist(title, artist)) {
            return -1;
        }

        int genreId = getGenreIdByName(genre);
        if (genreId == -1) {
            genreId = addGenre(genre);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_TITLE, title);
        values.put(COLUMN_SONG_ARTIST, artist);
        values.put(COLUMN_SONG_GENRE, genreId);
        values.put(COLUMN_SONG_PATH, path);

        long newRowId = db.insert(TABLE_SONGS, null, values);
        db.close();

        return newRowId;
    }

    public List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<Song>();
        String SELECT_QUERY = "SELECT * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(SELECT_QUERY, null);
        if (c.moveToFirst()) {
            do {
                Song s = new Song();
                s.setId(c.getInt(0));
                s.setTitle(c.getString(1));
                s.setArtist(c.getString(2));
                int genreId = c.getInt(c.getColumnIndexOrThrow(COLUMN_SONG_GENRE));
                Genre genre = getGenreById(genreId);
                s.setGenre(genre);
                s.setPath(c.getInt(4));

                songList.add(s);
            } while (c.moveToNext());
        }
        return songList;
    }

    public boolean isSongExist(String title, String artist) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_SONG_TITLE + " = ? AND " + COLUMN_SONG_ARTIST + " = ?";
        String[] selectionArgs = { title, artist };

        Cursor cursor = db.query(TABLE_SONGS, null, selection, selectionArgs, null, null, null);

        boolean isExist = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isExist;
    }

    public boolean isGenreExist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_GENRE_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(TABLE_GENRES, null, selection, selectionArgs, null, null, null);

        boolean isExist = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isExist;
    }

    public void deleteAllSongs() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONGS, null, null);
        db.close();
    }

    // THỂ LOẠI NHẠC
    public int addGenre(String name) {
        if (isGenreExist(name)) {
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENRE_NAME, name);
        return (int) db.insert(TABLE_GENRES, null, values);
    }

    public int getGenreIdByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_GENRE_ID + " FROM " + TABLE_GENRES
                + " WHERE " + COLUMN_GENRE_NAME + " = ?";

        Cursor c = db.rawQuery(query, new String[] { name });
        int genreId = -1;
        if (c.moveToFirst()) {
            genreId = c.getInt(c.getColumnIndexOrThrow(COLUMN_GENRE_ID));
        }
        c.close();
        return genreId;
    }

    private Genre getGenreById(int genreId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { COLUMN_GENRE_ID, COLUMN_GENRE_NAME };
        String selection = COLUMN_GENRE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(genreId) };
        Cursor cursor = db.query(TABLE_GENRES, projection, selection, selectionArgs, null, null, null);
        Genre genre = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GENRE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE_NAME));
            genre = new Genre(id, name);
            cursor.close();
        }
        return genre;
    }

    public List<Genre> getAllGenres() {
        List<Genre> genreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { COLUMN_GENRE_ID, COLUMN_GENRE_NAME };
        Cursor cursor = db.query(TABLE_GENRES, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GENRE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE_NAME));

                Genre genre = new Genre();
                genre.setId(id);
                genre.setName(name);

                genreList.add(genre);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return genreList;
    }

    public List<Song> getSongsByGenre(int genreId) {
        List<Song> songList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_SONG_ID, COLUMN_SONG_TITLE, COLUMN_SONG_ARTIST, COLUMN_SONG_GENRE, COLUMN_SONG_PATH};

        String selection = COLUMN_SONG_GENRE + " = ?";
        String[] selectionArgs = {String.valueOf(genreId)};

        Cursor cursor = db.query(TABLE_SONGS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST));
                //String genreString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_GENRE));
                //int genreId = getGenreIdByName(genreString);
                Genre genre = getGenreById(genreId);
                int path = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_PATH));
                Song song = new Song(id, title, artist, genre, path);
                songList.add(song);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return songList;
    }


    // Thêm bài hát yêu thích cho người dùng
    public void addFavorite(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FAVORITE_SONG_ID, songId);
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    // Xóa bài hát yêu thích của người dùng
    public void removeFavorite(int userId, int songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_USER_ID + " = ? AND " + COLUMN_FAVORITE_SONG_ID + " = ?",
                new String[] { String.valueOf(userId), String.valueOf(songId) });
        db.close();
    }

    // Kiểm tra xem một bài hát có trong danh sách yêu thích của người dùng hay không
    public boolean isFavorite(int userId, int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, new String[] { COLUMN_FAVORITE_ID },
                COLUMN_USER_ID + " = ? AND " + COLUMN_FAVORITE_SONG_ID + " = ?",
                new String[] { String.valueOf(userId), String.valueOf(songId) },
                null, null, null, null);

        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }

    public List<Song> getFavoriteSongs(int userId) {
        List<Song> favoriteSongsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_ID + " = " + userId;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int songId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE_SONG_ID));
                Song song = getSongById(songId);
                favoriteSongsList.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteSongsList;
    }

    public Song getSongById(int songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS,
                new String[] { COLUMN_SONG_ID, COLUMN_SONG_TITLE, COLUMN_SONG_ARTIST, COLUMN_SONG_GENRE,
                        COLUMN_SONG_PATH },
                COLUMN_SONG_ID + "=?", new String[] { String.valueOf(songId) }, null, null, null, null);

        Song song = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST));
            String genreString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_GENRE));
            int genreId = getGenreIdByName(genreString);
            Genre genre = getGenreById(genreId);
            int path = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_PATH)));
            song = new Song(id, title, artist, genre, path);
            cursor.close();
        }

        db.close();

        return song;
    }

}
