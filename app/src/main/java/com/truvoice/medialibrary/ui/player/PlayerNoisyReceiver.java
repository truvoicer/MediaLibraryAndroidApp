package com.truvoice.medialibrary.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class PlayerNoisyReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    public PlayerNoisyReceiver(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            mediaPlayer.pause();
        }
    }
}
