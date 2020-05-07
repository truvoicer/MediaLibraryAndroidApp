package com.truvoice.medialibrary.recycleviews;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;
import com.truvoice.medialibrary.recycleviews.adapters.LibraryRecyclerAdapterBase;
import com.truvoice.medialibrary.recycleviews.adapters.LibraryRecyclerMediaAdapter;
import com.truvoice.medialibrary.ui.library.LibraryActionModeCallback;

import java.util.List;

public class RecycleViewBuilder implements LibraryRecyclerAdapterBase.LibraryRecyclerAdapterListeners {
    private static final String TAG = "RecycleViewBuilder";
    private static final String RECYCLE_SELECTION_ID = "library_recycle_selection";

    private View view;
    private Context context;
    private Activity activity;
    private MediaLibraryManager mediaLibraryManager;
    private int recyclerViewResource;
    private RecyclerView recyclerView;
    private LibraryRecyclerMediaAdapter mediaAdapter = null;
    private SelectionTracker selectionTracker = null;
    private List data;
    private ImageView listBackButton;


    private PopupMenuListeners listeners;
    public interface PopupMenuListeners {
        void OnPopupClick(View view, Object item);
        void OnListBackButtonClick();
    }
    public RecycleViewBuilder(View view, Context context, int recyclerViewResource,
                              MediaLibraryManager mediaLibraryManager, PopupMenuListeners listeners) {
        this.view = view;
        this.context = context;
        this.recyclerViewResource = recyclerViewResource;
        this.mediaLibraryManager = mediaLibraryManager;
        this.listeners = listeners;
        buildRecyclerViewResource();
    }

    private void buildRecyclerViewResource() {
        recyclerView = (RecyclerView) view.findViewById(recyclerViewResource);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.listBackButton = (ImageView) view.findViewById(R.id.library_list_back_button);
        View.OnClickListener backButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeners.OnListBackButtonClick();
            }
        };
        listBackButton.setOnClickListener(backButtonListener);
    }
    public void showBackButton() {
        listBackButton.setVisibility(View.VISIBLE);
    }
    public void hideBackButton() {
        listBackButton.setVisibility(View.GONE);
    }
    public void buildMediaAdapter() {
            mediaAdapter = new LibraryRecyclerMediaAdapter(mediaLibraryManager.getCurrentTabSection(), this);
            mediaAdapter.setCategory(mediaLibraryManager.getCurrentTabSection().getCategory());
            recyclerView.setAdapter(mediaAdapter);
    }

    public void buildSelectionTracker(Activity activity, OnItemActivatedListener listener,
                                       LibraryActionModeCallback.LibraryActionModeListeners callback) {
        selectionTracker = new SelectionTracker.Builder<>(
                RECYCLE_SELECTION_ID,
                recyclerView,
                new SelectionKeyProvider(0, mediaAdapter),
                new LibraryRecyclerLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(listener)
                .build();
        mediaAdapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new LibrarySelectionTracker(activity, selectionTracker, callback));
    }

    public void updateAdapterData(List data) {
        this.data = data;
        mediaAdapter.updateData(data);
    }

    public LibraryRecyclerMediaAdapter getMediaAdapter() {
        return mediaAdapter;
    }

    @Override
    public void onPopupMenuClicked(View view, Object item) {
        listeners.OnPopupClick(view, item);
    }

}
