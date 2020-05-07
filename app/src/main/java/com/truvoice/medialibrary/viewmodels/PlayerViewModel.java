package com.truvoice.medialibrary.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.entities.VideoQueue;
import com.truvoice.medialibrary.repositories.MediaItemRepository;
import com.truvoice.medialibrary.repositories.QueueRepository;

import java.util.List;

public class PlayerViewModel extends AndroidViewModel {
    private static final String TAG = "PlayerViewModel";
    private QueueRepository queueRepository;
    private Media mediaItem;
    private LiveData<List<AudioQueue>> audioQueueItems = null;
    private LiveData<List<VideoQueue>> videoQueueItems = null;

    public PlayerViewModel(Application application) {
        super(application);
        queueRepository = new QueueRepository(application);
    }

    public LiveData<List<AudioQueue>> getAudioQueueItemsFromDb() {
        audioQueueItems = queueRepository.getAllAudioQueueItems();
        return audioQueueItems;
    }

    public LiveData<List<AudioQueue>> getAudioQueueItems() {
        return audioQueueItems;
    }

    public Media getMediaItemByAudioQueuePos(int position) {
        Media mediaItem = MediaItemRepository.getMediaItem(getApplication(),
                audioQueueItems.getValue().get(position).getMediaId());
        return mediaItem;
    }

    public LiveData<List<VideoQueue>> getVideoQueueItemsFromDb() {
        videoQueueItems = queueRepository.getAllVideoQueueItems();
        return videoQueueItems;
    }

    public LiveData<List<VideoQueue>> getVideoQueueItems() {
        return videoQueueItems;
    }

    public void deleteAudioQueueItem(AudioQueue audioQueueItem) {
        queueRepository.deleteAudioQueueItem(audioQueueItem);
    }

    public void deleteVideoQueueItem(VideoQueue videoQueueItem) {
        queueRepository.deleteVideoQueueItem(videoQueueItem);
    }
}