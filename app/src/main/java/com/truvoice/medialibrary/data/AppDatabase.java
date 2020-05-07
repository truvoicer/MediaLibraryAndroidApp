package com.truvoice.medialibrary.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.AudioQueueDao;
import com.truvoice.medialibrary.entities.Playlist;
import com.truvoice.medialibrary.entities.PlaylistDao;
import com.truvoice.medialibrary.entities.PlaylistMedia;
import com.truvoice.medialibrary.entities.PlaylistMediaDao;
import com.truvoice.medialibrary.entities.VideoQueue;
import com.truvoice.medialibrary.entities.VideoQueueDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AudioQueue.class, VideoQueue.class, Playlist.class, PlaylistMedia.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "MediaLibraryDatabase";
    public static final String DB_NAME = "medialibrarydb";

    public abstract AudioQueueDao AudioQueueDao();
    public abstract VideoQueueDao VideoQueueDao();
    public abstract PlaylistDao PlaylistDao();
    public abstract PlaylistMediaDao PlaylistMediaDao();

    private static volatile  AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME).build();
                }
            }
        }
        return instance;
    }


}
