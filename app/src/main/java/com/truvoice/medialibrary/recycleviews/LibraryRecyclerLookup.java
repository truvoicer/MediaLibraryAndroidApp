package com.truvoice.medialibrary.recycleviews;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.truvoice.medialibrary.recycleviews.adapters.LibraryRecyclerMediaAdapter;

public class LibraryRecyclerLookup extends ItemDetailsLookup {
    private static final String TAG = "LibraryRecyclerLookup";

    private final RecyclerView recyclerView;

    public LibraryRecyclerLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof LibraryRecyclerMediaAdapter.LibraryViewHolder) {
//                Log.d(TAG, "getItemDetails: " + ((LibraryRecyclerAdapter.LibraryViewHolder) holder).getItemDetails());
                return ((LibraryRecyclerMediaAdapter.LibraryViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
