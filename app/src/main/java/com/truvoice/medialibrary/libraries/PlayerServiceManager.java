package com.truvoice.medialibrary.libraries;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.repositories.MediaItemRepository;
import com.truvoice.medialibrary.services.PlayerService;
import com.truvoice.medialibrary.ui.player.ConnectionCallback;

import java.util.List;
import java.util.Objects;

public class PlayerServiceManager implements ConnectionCallback.TransportControls,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayerService";
    public static final String PLAY_BUNDLE_MEDIA_ITEM = "PLAY_BUNDLE_MEDIA_ITEM";
    public static final String PLAY_BUNDLE_IS_QUEUE_ITEM = "PLAY_BUNDLE_IS_QUEUE_ITEM";
    public static final String PLAY_BUNDLE_QUEUE_POS = "PLAY_BUNDLE_IS_QUEUE_POS";

    private View view;
    private Activity activity;
    private MediaBrowserCompat mediaBrowser;
    private Context context;
    private MediaControllerCompat mediaController;
    private PlayerServiceCallbacks callback;

    public interface PlayerServiceCallbacks {
        void buildTransportControls();
    }
    public PlayerServiceManager(View view, Activity activity, Context context, PlayerServiceCallbacks callback) {
        this.view = view;
        this.activity = activity;
        this.context = context;
        this.callback = callback;
        mediaBrowserInit();
    }

    private void mediaBrowserInit() {
        mediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context, PlayerService.class),
                new ConnectionCallback(this),
                null);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMediaBrowserConnected() {
        try {
            Log.d(TAG, "onConnected: 1" + mediaBrowser);

            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

            mediaController = new MediaControllerCompat(activity, token);

            MediaControllerCompat.setMediaController(activity, mediaController);

        } catch (RemoteException e) {
            Log.d(TAG, "onConnected: Remote Exception: " + e.getMessage());
        } finally {
            callback.buildTransportControls();
        }
    }

    private void playMediaItem(Media mediaItem) {
        Bundle playBundle = new Bundle();
        playBundle.putSerializable(PLAY_BUNDLE_MEDIA_ITEM, mediaItem);
        MediaControllerCompat.getMediaController(Objects.requireNonNull(activity))
                .getTransportControls()
                .playFromMediaId(mediaItem.getMediaId(), playBundle);
    }


    public void addQueueToSession(List queueList) {
        mediaController = MediaControllerCompat.getMediaController(activity);
        for (Object itemObject : queueList) {
            if (itemObject instanceof AudioQueue) {
                AudioQueue audioQueueItem = (AudioQueue) itemObject;
                Media mediaItem = MediaItemRepository.getMediaItem(activity.getApplication(),
                        audioQueueItem.getMediaId());
                Uri uri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Integer.parseInt(mediaItem.getMediaId()));
                MediaDescriptionCompat.Builder mediaDescriptionBuilder = new MediaDescriptionCompat.Builder()
                        .setMediaId(mediaItem.getMediaId())
                        .setMediaUri(uri)
                        .setTitle(mediaItem.getName())
                        .setSubtitle(mediaItem.getAlbum());
                mediaController.addQueueItem(mediaDescriptionBuilder.build());
            }
        }

    }

    public void start() {
        if (mediaBrowser != null) {
            mediaBrowser.connect();
        }
    }

    public void disconnect() {
//        if (MediaControllerCompat.getMediaController(getActivity()) != null) {
//            MediaControllerCompat.getMediaController(getActivity()).unregisterCallback();
//        }
        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
    }

    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }


    public MediaControllerCompat getMediaController() {
        return mediaController;
    }
}
