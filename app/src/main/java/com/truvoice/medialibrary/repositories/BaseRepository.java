package com.truvoice.medialibrary.repositories;

import android.app.Application;
import android.database.Cursor;

import com.truvoice.medialibrary.data.AppDatabase;

public class BaseRepository {
    private static final String TAG = "PlaylistRepository";

    protected Application application;
    protected AppDatabase db;

    public interface RepositoryCursorListener {
        void OnDialogListCursorFetch(Cursor cursor);
    }

    public BaseRepository(Application application) {
        this.application = application;
        db = AppDatabase.getDatabase(application);
    }

}
