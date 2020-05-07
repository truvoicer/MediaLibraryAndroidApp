package com.truvoice.medialibrary.ui.player;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.SeekBar;
import android.widget.TextView;

import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.enums.LibraryMediaType;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;
import com.truvoice.medialibrary.libraries.PlayerServiceManager;

import java.util.Objects;

public class PlayerManager implements  SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayerManager";
    private static final String DEFAULT_SEEK_INTERVAL = "1000";
    private static final String INITIAL_SEEK_PROGRESS = "0";

    private CountDownTimer countDownTimer;
    private SeekBar audioSeekBar;

    private TextView audioTitle;
    private TextView audioAlbum;
    private TextView audioArtist;
    private TextView audioDuration;
    private TextView audioSeekProgress;

    private Context context;
    private Activity activity;

    public PlayerManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public void initPlay(Media mediaItem) {
        String duration = MediaLibraryManager.getMediaDuration(context, mediaItem.getMediaId(), LibraryMediaType.AUDIO);

        configureAudioSeekBar(0, Long.parseLong(duration), Long.parseLong(DEFAULT_SEEK_INTERVAL));

        setDisplayText(mediaItem.getName(),  mediaItem.getAlbum(), mediaItem.getArtist(),
                MediaLibraryManager.getFormattedDuration(duration), INITIAL_SEEK_PROGRESS);
    }

    public void playMediaItem(Media mediaItem, boolean isQueueItem, int position) {
        initPlay(mediaItem);

        Bundle playBundle = new Bundle();
        playBundle.putSerializable(PlayerServiceManager.PLAY_BUNDLE_MEDIA_ITEM, mediaItem);
        playBundle.putBoolean(PlayerServiceManager.PLAY_BUNDLE_IS_QUEUE_ITEM, isQueueItem);
        playBundle.putInt(PlayerServiceManager.PLAY_BUNDLE_QUEUE_POS, position);
        MediaControllerCompat.getMediaController(Objects.requireNonNull(activity))
                .getTransportControls()
                .playFromMediaId(mediaItem.getMediaId(), playBundle);
        startSeekbarSync();
    }

    public void audioSeekBarInit(SeekBar audioSeekBar, TextView audioTitle,TextView audioAlbum,
                                 TextView audioArtist, TextView audioDuration, TextView audioSeekProgress) {
        this.audioSeekBar = audioSeekBar;
        this.audioTitle = audioTitle;
        this.audioAlbum = audioAlbum;
        this.audioArtist = audioArtist;
        this.audioDuration = audioDuration;
        this.audioSeekProgress = audioSeekProgress;
        this.audioSeekBar.setMin(0);
        this.audioSeekBar.setMax(0);
    }

    public void setDisplayText(String audioTitleText, String audioAlbumText, String audioArtistText,
                                String audioDurationText, String audioSeekProgressText) {
        audioTitle.setText(audioTitleText);
        audioAlbum.setText(audioAlbumText);
        audioArtist.setText(audioArtistText);
        audioDuration.setText(audioDurationText);
        audioSeekProgress.setText(audioSeekProgressText);
    }

    private void configureAudioSeekBar(int min, Long duration, Long interval) {
        this.audioSeekBar.setMin(min);
        this.audioSeekBar.setMax(Integer.parseInt(duration.toString()));
        Long futureTime = System.currentTimeMillis() + duration;
        syncSeekbar(futureTime, interval);
        this.audioSeekBar.setOnSeekBarChangeListener(this);
    }

    public void syncSeekbar(Long futureTime, Long interval) {
        countDownTimer = new CountDownTimer(futureTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Long playbackPos = MediaControllerCompat.getMediaController(activity).getPlaybackState().getPosition();
                audioSeekBar.setProgress(Integer.parseInt(playbackPos.toString()));
            }

            @Override
            public void onFinish() {

            }
        };
    }

    public void stopSeekbarSync() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void startSeekbarSync() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.d(TAG, "onProgressChanged: called");
//        Log.d(TAG, "onProgressChanged: progress: " + seekBar.getProgress());
        audioSeekProgress.setText(MediaLibraryManager.getFormattedDuration(String.valueOf(seekBar.getProgress())));
        MediaControllerCompat.getMediaController(activity).getTransportControls().seekTo((long) progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
