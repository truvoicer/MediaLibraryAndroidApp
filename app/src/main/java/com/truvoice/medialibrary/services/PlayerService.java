package com.truvoice.medialibrary.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.repositories.QueueRepository;
import com.truvoice.medialibrary.ui.player.PlayerSessionCallbacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerService extends MediaBrowserServiceCompat implements MediaPlayer.OnPreparedListener,
                            MediaPlayer.OnErrorListener {
    private static final String TAG = "PlayerService";

    public static final String PLAYER_SERVICE_ROOT_ID = "PLAYER_SERVICE_ROOT_ID";
    public static final String PLAYER_SERVICE_EMPTY_ROOT_ID = "PLAYER_SERVICE_EMPTY_ROOT_ID";
    public static final String CHANNEL_ID = "tru_music_channel";

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    private QueueRepository queueRepository;
    public List<AudioQueue> audioQueueList;
    private NotificationChannel channel;
    private MediaControllerCompat controller;
    public  MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Queue list:" + audioQueueList);

        createNotificationChannel();
        mediaSession = new MediaSessionCompat(this, TAG);

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        MediaSessionCompat.Token token =  mediaSession.getSessionToken();

        setSessionToken(token);

        controller = mediaSession.getController();

        playbackStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_SEEK_TO |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SET_REPEAT_MODE |
                        PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_URI);
        mediaSession.setPlaybackState(playbackStateBuilder.build());

        MediaSessionCompat.fromMediaSession(this, mediaSession.getMediaSession())
                .setCallback(new PlayerSessionCallbacks(getApplicationContext(), mediaSession, this));
    }

    public Notification buildNotification(PendingIntent intent) {
        Log.d(TAG, "onCreate: channel id: " + channel.getId());

        MediaMetadataCompat mediaMetadataCompat = controller.getMetadata();
        MediaDescriptionCompat descriptionCompat = mediaMetadataCompat.getDescription();
        Log.d(TAG, "buildNotification: Metadata description: " + descriptionCompat.getTitle());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel.getId());
        builder
                .setSmallIcon(R.drawable.baseline_radio_black_18)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(descriptionCompat.getTitle())
                    .setContentText(descriptionCompat.getSubtitle())
                    .setSubText(descriptionCompat.getDescription())

                .setContentIntent(intent)

                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .addAction(new NotificationCompat.Action(
                        R.drawable.baseline_skip_previous_black_24, getString(R.string.play_button_text),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.baseline_play_arrow_black_24, getString(R.string.play_button_text),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.baseline_pause_circle_outline_black_24, getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PAUSE)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.baseline_stop_black_24, getString(R.string.stop_button_text),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_STOP)
                ))

                .addAction(new NotificationCompat.Action(
                        R.drawable.baseline_skip_next_black_24, getString(R.string.stop_button_text),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                ))

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(getSessionToken())
                        .setShowActionsInCompactView(0));
        return builder.build();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationChannel = getSystemService(NotificationManager.class);
            notificationChannel.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot: called");
        return new BrowserRoot(PLAYER_SERVICE_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren:  " + parentId + " " + result);

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        MediaDescriptionCompat mediaDescription =
                new MediaDescriptionCompat.Builder()
                .setMediaId(String.valueOf(81))
                .setDescription("Description")
                .setTitle("Title")
                .setSubtitle("Subtitle")
                .build();

        MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(mediaDescription,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE | MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);


        mediaItems.add(mediaItem);
        result.sendResult(mediaItems);
    }


    public void buildMediaPlayer(Uri uri) {
        Log.d(TAG, "buildMediaPlayer: called");
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setOnPreparedListener(this);
        }
        catch (IOException e) {
            Log.d(TAG, "buildMediaPlayer: IOException: " + e.getMessage());
        }

    }

    public void startMediaPlayer() {
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: Media player prepared");
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: called");
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d(TAG, "onError: Media player unknown error");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d(TAG, "onError: Media player Server died");
                break;
        }
        return false;
   }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) mediaPlayer.release();
    }

    public List<AudioQueue> getAudioQueueList() {
        return audioQueueList;
    }

    public void setAudioQueueList(List<AudioQueue> audioQueueList) {
        this.audioQueueList = audioQueueList;
    }

    public MediaControllerCompat getController() {
        return controller;
    }

    public PlaybackStateCompat.Builder getPlaybackStateBuilder() {
        return playbackStateBuilder;
    }
}
