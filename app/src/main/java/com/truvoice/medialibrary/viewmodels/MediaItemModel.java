package com.truvoice.medialibrary.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.truvoice.medialibrary.repositories.MediaItemRepository;

public class MediaItemModel extends AndroidViewModel {
    private static final String TAG = "MediaItemModel";

    private MediaItemRepository mediaItemRepository;

    public MediaItemModel(Application application) {
        super(application);
        Log.d(TAG, "MediaItemModel: start");
        mediaItemRepository = new MediaItemRepository(application);
    }

}
