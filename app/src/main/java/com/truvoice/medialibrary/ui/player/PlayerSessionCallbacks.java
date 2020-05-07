package com.truvoice.medialibrary.ui.player;

import android.content.ContentUris;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.libraries.PlayerServiceManager;
import com.truvoice.medialibrary.repositories.MediaItemRepository;
import com.truvoice.medialibrary.services.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class PlayerSessionCallbacks extends MediaSessionCompat.Callback {
    private static final String TAG = "PlayerSessionCallbacks";

    private Context context;
    private AudioFocusRequest audioFocusRequest;
    private MediaSessionCompat mediaSession;
    private PlayerService playerService;
    private Media currentMediaItem;
    private IntentFilter noisyIntent;
    private PlayerNoisyReceiver playerNoisyReceiver;
    private List<MediaSessionCompat.QueueItem> queueItemList;
    private int queueCount = 0;
    private boolean isQueue = false;
    private int currentQueuePos;

    public PlayerSessionCallbacks(Context context, MediaSessionCompat mediaSession,
                                   PlayerService playerService) {
        this.context = context;
        this.mediaSession = mediaSession;
        this.playerService = playerService;
        this.noisyIntent = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        this.playerNoisyReceiver = new PlayerNoisyReceiver(this.playerService.mediaPlayer);
        queueItemList = new ArrayList<>();
    }

    private void audioInit() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(audioAttributes)
                .build();
        int result = audioManager.requestAudioFocus(audioFocusRequest);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            throw new IllegalStateException("Audio focus request not granted");
        }
    }

    private void buildMetadata() {
        MediaMetadataCompat.Builder metaDataBuilder = new MediaMetadataCompat.Builder();
        MediaMetadataCompat metadata = metaDataBuilder
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, currentMediaItem.getMediaId())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentMediaItem.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentMediaItem.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_DATE, currentMediaItem.getDateAdded())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentMediaItem.getName())
                .build();
        mediaSession.setMetadata(metadata);
    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle extras) {
        audioInit();
        currentMediaItem = (Media) extras.getSerializable(PlayerServiceManager.PLAY_BUNDLE_MEDIA_ITEM);
        buildMetadata();
        if (playerService.mediaPlayer != null) {
            if (playerService.mediaPlayer.isPlaying()) {
                playerService.mediaPlayer.stop();
            }
        }
        playerService.buildMediaPlayer(uri);
        playerService.startMediaPlayer();
        playerStartTasks();
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        Log.d(TAG, "onPlayFromMediaId: called");
        audioInit();
        currentMediaItem =  getCurrentMediaItem(extras);
        buildMetadata();
        Uri uri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Integer.parseInt(currentMediaItem.getMediaId()));

        if (playerService.mediaPlayer != null) {
            if (playerService.mediaPlayer.isPlaying()) {
                playerService.mediaPlayer.stop();
            }
        }
        playerService.buildMediaPlayer(uri);
        playerService.startMediaPlayer();
        playerStartTasks();
    }

    private Media getCurrentMediaItem(Bundle extras) {
        if(extras.getBoolean(PlayerServiceManager.PLAY_BUNDLE_IS_QUEUE_ITEM)) {
            int queuePos = extras.getInt(PlayerServiceManager.PLAY_BUNDLE_QUEUE_POS);
            String mediaId = playerService.getController().getQueue().get(queuePos).getDescription().getMediaId();
            currentQueuePos = queuePos;
            isQueue = true;
            return MediaItemRepository.getMediaItem(playerService.getApplication(), mediaId);
        } else {
            currentQueuePos = 0;
            return (Media) extras.getSerializable(PlayerServiceManager.PLAY_BUNDLE_MEDIA_ITEM);
        }
    }

    private void playerStartTasks() {
//        playerService.registerReceiver(playerNoisyReceiver, noisyIntent);
        mediaSession.setPlaybackState(playerService.getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1)
                .setActiveQueueItemId(currentQueuePos)
                .build());
        mediaSession.setActive(true);
        playerService.startForeground(1, playerService.buildNotification(playerService.getController().getSessionActivity()));
    }

    @Override
    public void onPlay() {
        Log.d(TAG, "onPlay: Session play called: ");
        audioInit();

        if (playerService.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED) {
            playerService.startMediaPlayer();
        } else {
            playerService.mediaPlayer.start();
        }
        mediaSession.setPlaybackState(playerService.getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_PLAYING, playerService.mediaPlayer.getCurrentPosition(), 1)
                .setActiveQueueItemId(currentQueuePos)
                .build());
        mediaSession.setActive(true);
        playerService.startForeground(1, playerService.buildNotification(playerService.getController().getSessionActivity()));
    }


    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        if (description.getMediaId() != null) {
            MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, queueCount);
            queueItemList.add(queueItem);
            mediaSession.setQueue(queueItemList);
            queueCount++;
        }
    }

    @Override
    public void onSkipToNext() {
        Log.d(TAG, "onSkipToNext: " + currentMediaItem);
        if (playerService.mediaPlayer != null) {
            currentQueuePos++;
            String mediaId = playerService.getController().getQueue().get(currentQueuePos).getDescription().getMediaId();
            Bundle playBundle = new Bundle();
            playBundle.putBoolean(PlayerServiceManager.PLAY_BUNDLE_IS_QUEUE_ITEM, true);
            playBundle.putInt(PlayerServiceManager.PLAY_BUNDLE_QUEUE_POS, currentQueuePos);
            onPlayFromMediaId(mediaId, playBundle);
        }
    }

    @Override
    public void onSkipToPrevious() {
        if (playerService.mediaPlayer != null && currentQueuePos != 0) {
            currentQueuePos--;
            String mediaId = playerService.getController().getQueue().get(currentQueuePos).getDescription().getMediaId();
            Bundle playBundle = new Bundle();
            playBundle.putBoolean(PlayerServiceManager.PLAY_BUNDLE_IS_QUEUE_ITEM, true);
            playBundle.putInt(PlayerServiceManager.PLAY_BUNDLE_QUEUE_POS, currentQueuePos);
            onPlayFromMediaId(mediaId, playBundle);
        }
    }

    @Override
    public void onSeekTo(long pos) {
        if (playerService.mediaPlayer != null) {
            playerService.mediaPlayer.seekTo((int) pos);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        if (playerService.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocusRequest(audioFocusRequest);

            mediaSession.setPlaybackState(playerService.getPlaybackStateBuilder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, playerService.mediaPlayer.getCurrentPosition(), 1)
                    .setActiveQueueItemId(currentQueuePos)
                    .build());
            playerService.stopSelf();

            playerService.mediaPlayer.pause();

            mediaSession.setActive(false);

            playerService.stopForeground(false);
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called: " + playerService.getController().getPlaybackState().getState());
        if (playerService.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ||
                playerService.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocusRequest(audioFocusRequest);


//            playerService.unregisterReceiver(playerNoisyReceiver);

            playerService.stopSelf();

            playerService.mediaPlayer.stop();

            mediaSession.setActive(false);

            playerService.stopForeground(false);
        }
        mediaSession.setPlaybackState(playerService.getPlaybackStateBuilder()
                .setState(PlaybackStateCompat.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1)
                .build());
    }
}
