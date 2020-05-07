package com.truvoice.medialibrary.entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoQueueDao {
    @Query("SELECT * FROM video_queue")
    public LiveData<List<VideoQueue>> getAllVideoQueueItems();

    @Query("SELECT * FROM video_queue ORDER BY :columnName")
    public LiveData<List<VideoQueue>> getAllVideoQueueItemsSorted(String columnName);

    @Insert
    void insertVideoQueueItem(VideoQueue... videoQueues);

    @Delete
    void deleteVideoQueueItem(VideoQueue... videoQueues);
}
