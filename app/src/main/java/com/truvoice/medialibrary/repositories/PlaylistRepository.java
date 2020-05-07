package com.truvoice.medialibrary.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.truvoice.medialibrary.data.AppDatabase;
import com.truvoice.medialibrary.entities.Playlist;
import com.truvoice.medialibrary.entities.PlaylistDao;
import com.truvoice.medialibrary.entities.PlaylistMedia;
import com.truvoice.medialibrary.entities.PlaylistMediaDao;

import java.util.List;

public class PlaylistRepository extends BaseRepository {
    private static final String TAG = "PlaylistRepository";

    private PlaylistDao playlistDao;
    private PlaylistMediaDao playlistMediaDao;
    private PlaylistRepositoryListener listener;

    public interface PlaylistRepositoryListener {
        void getPlaylistMediaById(List<PlaylistMedia> playlistMediaList);
    }
    public PlaylistRepository(Application application) {
        super(application);
        playlistDao = db.PlaylistDao();
        playlistMediaDao = db.PlaylistMediaDao();
    }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylists();
    }

    public LiveData<List<PlaylistMedia>> getAllPlaylistsMediaRefs() {
        return playlistMediaDao.getAllPlaylistMedia();
    }

    public LiveData<List<PlaylistMedia>> getMediaByPlaylistId(int playlistId) {
        return playlistMediaDao.getMediaByPlaylistId(playlistId);
    }

    public void insertPlaylist(final Playlist playlist) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Insert playlist: " + playlist);
                playlistDao.insertPlaylist(playlist);
            }
        });
    }

    public void insertPlaylistMedia(final PlaylistMedia playlistMedia) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: Insert playlistMedia: " + playlistMedia);
                playlistMediaDao.insertPlaylistMedia(playlistMedia);
            }
        });
    }

    public void getAllPlaylistCursor(final RepositoryCursorListener listener) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: cursor: ");
//                playlistMediaDao.insertPlaylistMedia(playlistMedia);
                listener.OnDialogListCursorFetch(playlistDao.getAllPlaylistsCursor());
            }
        });
    }

}
