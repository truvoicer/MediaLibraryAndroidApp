package com.truvoice.medialibrary.entities;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    public LiveData<List<Playlist>> getAllPlaylists();

    @Query("SELECT * FROM playlist")
    Cursor getAllPlaylistsCursor();

    @Query("SELECT * FROM playlist ORDER BY :columnName")
    public LiveData<List<Playlist>> getAllPlaylistsSorted(String columnName);

    @Insert
    void insertPlaylist(Playlist... playlist);

    @Delete
    void deletePlaylist(Playlist... playlist);

}
