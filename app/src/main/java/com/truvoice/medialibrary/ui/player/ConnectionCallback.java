package com.truvoice.medialibrary.ui.player;

import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

public class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
    private static final String TAG = "ConnectionCallback";

    private TransportControls callback;

    public interface TransportControls {
        void onMediaBrowserConnected();
    }

    public ConnectionCallback(TransportControls callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected: called");
        callback.onMediaBrowserConnected();
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }
}
