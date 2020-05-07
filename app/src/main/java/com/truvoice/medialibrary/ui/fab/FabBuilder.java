package com.truvoice.medialibrary.ui.fab;

import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;

public class FabBuilder implements View.OnClickListener {
    private static final String TAG = "FabBuilder";
    
    private MediaLibraryManager mediaLibraryManager;
    private View view;
    
    private int fabResource;
    private FloatingActionButton floatingActionButton;
    
    private int titleStringRes;
    private int duration;
    private int actionLabelRes;
    private SnackbarOnClickListener listener;

    public interface SnackbarOnClickListener {
        void OnFabActionClick(View view);
    }

    public FabBuilder(View view, int fabResource, int titleStringRes, int duration, int actionLabelRes,
                      SnackbarOnClickListener listener) {
        this.view = view;
        this.fabResource = fabResource;
        this.titleStringRes = titleStringRes;
        this.duration = duration;
        this.actionLabelRes = actionLabelRes;
        this.listener = listener;
        initFab();
        buildFab();
    }

    private void initFab() {
        floatingActionButton = (FloatingActionButton) view.findViewById(fabResource);
    }
    
    private void buildFab() {
        Log.d(TAG, "addListener: add listener");
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: fab click");
        Snackbar.make(v, titleStringRes, duration)
        .setAction(actionLabelRes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: start");
                listener.OnFabActionClick(v);
            }
        }).show();
    }
}
