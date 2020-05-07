package com.truvoice.medialibrary.recycleviews;

import android.app.Activity;
import android.util.Log;
import android.view.ActionMode;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.ui.library.LibraryActionModeCallback;

public class LibrarySelectionTracker extends SelectionTracker.SelectionObserver {
    private static final String TAG = "LibrarySelectionTracker";
    private Activity activity;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode;
    private LibraryActionModeCallback.LibraryActionModeListeners callback;

    public LibrarySelectionTracker(Activity activity, SelectionTracker selectionTracker, LibraryActionModeCallback.LibraryActionModeListeners callback) {
        Log.d(TAG, "LibrarySelectionTracker: start");
        this.activity = activity;
        this.selectionTracker = selectionTracker;
        this.callback = callback;
    }

    @Override
    public void onItemStateChanged(@NonNull Object key, boolean selected) {
        super.onItemStateChanged(key, selected);
        Log.d(TAG, "onItemStateChanged: called");
    }

    @Override
    public void onSelectionRefresh() {
        super.onSelectionRefresh();
        Log.d(TAG, "onSelectionRefresh: called");
    }

    @Override
    public void onSelectionChanged() {
        super.onSelectionChanged();
        Log.d(TAG, "onSelectionChanged: called");
        if (selectionTracker.hasSelection() && actionMode == null) {
            actionMode = activity.startActionMode(new LibraryActionModeCallback(selectionTracker, callback));
            actionMode.setTitle(activity.getString(R.string.items_selected_amount, selectionTracker.getSelection().size()));
        } else if (selectionTracker.hasSelection()) {
            actionMode.setTitle(activity.getString(R.string.items_selected_amount, selectionTracker.getSelection().size()));
        } else if (!selectionTracker.hasSelection() && actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    @Override
    public void onSelectionRestored() {
        super.onSelectionRestored();
        Log.d(TAG, "onSelectionRestored: called");
    }
}

