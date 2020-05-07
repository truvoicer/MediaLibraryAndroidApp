package com.truvoice.medialibrary.recycleviews;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.truvoice.medialibrary.recycleviews.adapters.LibraryRecyclerMediaAdapter;

import java.util.List;

public class SelectionKeyProvider extends ItemKeyProvider {
    private static final String TAG = "SelectionKeyProvider";
    private List itemList;
    private LibraryRecyclerMediaAdapter mediaAdapter;

    public SelectionKeyProvider(int scope, LibraryRecyclerMediaAdapter mediaAdapter) {
        super(scope);
        this.mediaAdapter = mediaAdapter;
        Log.d(TAG, "SelectionKeyProvider: itemList: " + itemList);
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return mediaAdapter.getData().get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return mediaAdapter.getData().indexOf(key);
    }

}
