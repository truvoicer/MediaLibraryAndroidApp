package com.truvoice.medialibrary.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.truvoice.medialibrary.data.AppDatabase;
import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.AudioQueueDao;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.entities.VideoQueue;
import com.truvoice.medialibrary.entities.VideoQueueDao;

import java.util.List;

public class QueueRepository extends BaseRepository {
    private static final String TAG = "QueueRepository";

    private AudioQueueDao audioQueueDao;
    private VideoQueueDao videoQueueDao;

    public QueueRepository(Application application) {
        super(application);
        audioQueueDao = db.AudioQueueDao();
        videoQueueDao = db.VideoQueueDao();
    }

    public LiveData<List<AudioQueue>> getAllAudioQueueItems() {
        return audioQueueDao.getAllAudioQueueItems();
    }

    public LiveData<List<AudioQueue>> getAllAudioQueueItemsSorted(String sortOrder) {
        return audioQueueDao.getAllAudioQueueItemsSorted(sortOrder);
    }

    public LiveData<List<VideoQueue>> getAllVideoQueueItems() {
        return videoQueueDao.getAllVideoQueueItems();
    }

    public LiveData<List<VideoQueue>> getAllVideoQueueItemsSorted(String sortOrder) {
        return videoQueueDao.getAllVideoQueueItemsSorted(sortOrder);
    }

    public Media getFirstAudioQueueItem() {
        Media mediaItem = MediaItemRepository.getMediaItem(application,
                audioQueueDao.getAllAudioQueueItems().getValue().get(0).getMediaId());
        Log.d(TAG, "getFirstAudioQueueItem: First queue Item: " + mediaItem);
        return mediaItem;
    }

    public void insertAudioQueueItem(final AudioQueue audioQueueItem) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Insert audio queueItem: " + audioQueueItem);
                audioQueueDao.insertAudioQueueItem(audioQueueItem);
            }
        });
    }

    public void deleteAudioQueueItem(final AudioQueue audioQueueItem) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Delete audio queueItem: " + audioQueueItem);
                audioQueueDao.deleteAudioQueueItem(audioQueueItem);
            }
        });
    }

    public void insertVideoQueueItem(final VideoQueue videoQueueItem) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: insert video queueItem: " + videoQueueItem);
                videoQueueDao.insertVideoQueueItem(videoQueueItem);
            }
        });
    }

    public void deleteVideoQueueItem(final VideoQueue videoQueueItem) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: delete video queueItem: " + videoQueueItem);
                videoQueueDao.deleteVideoQueueItem(videoQueueItem);
            }
        });
    }
}
