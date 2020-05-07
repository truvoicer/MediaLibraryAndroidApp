package com.truvoice.medialibrary.entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioQueueDao {
    @Query("SELECT * FROM audio_queue")
    public LiveData<List<AudioQueue>> getAllAudioQueueItems();

    @Query("SELECT * FROM audio_queue ORDER BY :columnName")
    public LiveData<List<AudioQueue>> getAllAudioQueueItemsSorted(String columnName);

    @Insert
    void insertAudioQueueItem(AudioQueue... audioQueue);

    @Delete
    void deleteAudioQueueItem(AudioQueue... audioQueue);

}
