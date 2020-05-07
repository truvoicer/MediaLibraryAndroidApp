package com.truvoice.medialibrary.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.entities.Playlist;
import com.truvoice.medialibrary.entities.PlaylistMedia;
import com.truvoice.medialibrary.entities.VideoQueue;
import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.enums.LibraryMediaType;
import com.truvoice.medialibrary.repositories.BaseRepository;
import com.truvoice.medialibrary.repositories.MediaItemRepository;
import com.truvoice.medialibrary.repositories.PlaylistRepository;
import com.truvoice.medialibrary.repositories.QueueRepository;

import java.util.ArrayList;
import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private static final String TAG = "LibraryViewModel";
    private MediaItemRepository mediaItemRepository;
    private QueueRepository queueRepository;
    private PlaylistRepository playListRepository;
    private MutableLiveData<List<Media>> allMediaItems = new MutableLiveData<List<Media>>();
    private LiveData<List<Playlist>> allPlaylists;
    public LiveData<List<PlaylistMedia>> playlistMediaRefs;
    private MutableLiveData<List<Media>> currentPlaylistMedia = new MutableLiveData<List<Media>>();

    public LibraryViewModel(Application application) {
        super(application);
        mediaItemRepository = new MediaItemRepository(application);
        queueRepository = new QueueRepository(application);
        playListRepository = new PlaylistRepository(application);
    }

    public MutableLiveData<List<Media>> updateMediaItems(LibraryMediaType mediaType, LibraryMediaCategory category, String sortOrder) {
        allMediaItems.setValue(mediaItemRepository.queryMediaItems(mediaType, category,
                null, null, sortOrder));
        return allMediaItems;
    }
    public MutableLiveData<List<Media>> getMediaItems() {
        return allMediaItems;
    }

    public List<Media> getMediaItemQuery(LibraryMediaType mediaType, LibraryMediaCategory category,
                                         String selection, String[] selectionArgs, String sortOrder) {
        return mediaItemRepository.queryMediaItems(mediaType, category, selection, selectionArgs,
                sortOrder);
    }

    public void addItemToAudioQueue(AudioQueue audioQueueItem) {
        queueRepository.insertAudioQueueItem(audioQueueItem);
    }

    public void addItemToVideoQueue(VideoQueue videoQueueItem) {
        queueRepository.insertVideoQueueItem(videoQueueItem);
    }

    public void insertNewPlaylist(Playlist playlist) {
        playListRepository.insertPlaylist(playlist);
    }

    public void insertPlaylistMediaRef(PlaylistMedia playlistMedia) {
        playListRepository.insertPlaylistMedia(playlistMedia);
    }

    public LiveData<List<Playlist>> updateAllPlaylists() {
        allPlaylists = playListRepository.getAllPlaylists();
        return allPlaylists;
    }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return allPlaylists;
    }

    public void getAllPlaylistsCursor(BaseRepository.RepositoryCursorListener listener) {
        playListRepository.getAllPlaylistCursor(listener);
    }

    public LiveData<List<PlaylistMedia>> updateAllPlaylistsMediaRefs() {
        playlistMediaRefs = playListRepository.getAllPlaylistsMediaRefs();
        return playlistMediaRefs;
    }

    public LiveData<List<PlaylistMedia>> getAllPlaylistsMediaRefs() {
        return playlistMediaRefs;
    }

    public List<Media> buildPlaylistMediaItems(List<PlaylistMedia> playlistMediaList, int id) {
        List<Media> mediaList = new ArrayList<>();
        for (PlaylistMedia item : playlistMediaList
             ) {
            if (item.getPlaylistId() == id) {
                Media mediaItem = MediaItemRepository.getMediaItem(getApplication(), item.getMediaId());
                mediaList.add(mediaItem);
            }
        }
        return mediaList;
    }

}