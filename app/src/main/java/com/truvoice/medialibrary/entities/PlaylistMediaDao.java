package com.truvoice.medialibrary.entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistMediaDao {
    @Query("SELECT * FROM playlist_media")
    public LiveData<List<PlaylistMedia>> getAllPlaylistMedia();

    @Query("SELECT * FROM playlist_media WHERE playlistId = :playlistId")
    public LiveData<List<PlaylistMedia>> getMediaByPlaylistId(int playlistId);

    @Insert
    void insertPlaylistMedia(PlaylistMedia... playlistMedia);

    @Delete
    void deletePlaylistMedia(PlaylistMedia... playlistMedia);

}
