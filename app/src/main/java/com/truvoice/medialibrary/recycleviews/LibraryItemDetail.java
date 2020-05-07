package com.truvoice.medialibrary.recycleviews;

import androidx.annotation.Nullable;

public class LibraryItemDetail extends LibraryRecyclerLookup.ItemDetails {
    private static final String TAG = "LibraryItemDetail";
    private final int adapterPosition;
    Object selectionKey;

    public LibraryItemDetail(int adapterPosition, Object selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }

}
