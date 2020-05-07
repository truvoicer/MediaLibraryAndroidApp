package com.truvoice.medialibrary.ui.library;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;

import com.truvoice.medialibrary.R;

public class LibraryActionModeCallback implements ActionMode.Callback {
    private static final String TAG = "LibraryActionModeCallba";

    private SelectionTracker selectionTracker;
    private LibraryActionModeListeners callback;

    public interface LibraryActionModeListeners {
        void OnActionModeAddToQueue(Selection selection);
        void OnActionModePlayItem(Selection selection);
        void OnActionModeDeleteItem(Selection selection);
        void OnActionModeAddToPlaylist(Selection selection);
    }

    public LibraryActionModeCallback(SelectionTracker selectionTracker, LibraryActionModeListeners callback) {
        this.selectionTracker = selectionTracker;
        this.callback = callback;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(TAG, "onCreateActionMode: start");
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.library_action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_add_to_queue:
                callback.OnActionModeAddToQueue(selectionTracker.getSelection());
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.mnu_add_to_playlist:
                callback.OnActionModeAddToPlaylist(selectionTracker.getSelection());
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.mnu_play_song:
                callback.OnActionModePlayItem(selectionTracker.getSelection());
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.mnu_delete:
                callback.OnActionModeDeleteItem(selectionTracker.getSelection());
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionTracker.clearSelection();
    }
}
