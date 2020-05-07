package com.truvoice.medialibrary.ui.player;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.libraries.PlayerServiceManager;
import com.truvoice.medialibrary.recycleviews.adapters.QueueRecyclerAdapter;
import com.truvoice.medialibrary.repositories.MediaItemRepository;
import com.truvoice.medialibrary.viewmodels.PlayerViewModel;

import java.util.List;

public class PlayerFragment extends Fragment implements QueueTouchListener.OnQueueTouchEvents,
        PlayerServiceManager.PlayerServiceCallbacks, View.OnClickListener {
    private static final String TAG = "PlayerFragment";
    public static final String RECYCLE_SELECTION_ID = "RECYCLE_SELECTION_ID";

    private MediaControllerCompat mediaController;
    private PlayerViewModel playerViewModel;
    private QueueRecyclerAdapter queueRecyclerAdapter;
    private RecyclerView queueRecyclerView;

    private SelectionTracker selectionTracker;
    private OnItemActivatedListener onItemActivatedListener;
    private PlayerServiceManager playerServiceManager;

    private PlayerManager playerManager;
    private ImageView btnPlayPause;
    private ImageView btnStop;
    private ImageView btnNext;
    private ImageView btnPrevious;
    private ImageView bthShuffle;
    private ImageView btnRepeat;

    private int queuePlayCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: start");
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        playerViewModel.getAudioQueueItemsFromDb().observe(getViewLifecycleOwner(), new Observer<List<AudioQueue>>() {
            @Override
            public void onChanged(List<AudioQueue> audioQueueList) {
                Log.d(TAG, "onChanged: Queue items: " + audioQueueList);
                queueRecyclerAdapter.updateQueueItems(audioQueueList);

            }
        });

        queueRecyclerView = getView().findViewById(R.id.queue_list_recyclerview);
        queueRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queueRecyclerAdapter = new QueueRecyclerAdapter(getContext(), getActivity().getApplication());
        queueRecyclerView.setAdapter(queueRecyclerAdapter);
        queueRecyclerView.addOnItemTouchListener(new QueueTouchListener(this, getContext(), queueRecyclerView));
        playerManager = new PlayerManager(getContext(), getActivity());
        playerManager.audioSeekBarInit(
                (SeekBar) getView().findViewById(R.id.audio_seek_bar),
                (TextView) getView().findViewById(R.id.audio_track_title),
                (TextView) getView().findViewById(R.id.audio_album_text),
                (TextView) getView().findViewById(R.id.audio_artist_text),
                (TextView) getView().findViewById(R.id.audio_seek_bar_duration_text),
                (TextView) getView().findViewById(R.id.audio_seek_bar_progress_text)
        );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        playerServiceManager = new PlayerServiceManager(
                getView(), getActivity(), getContext(), this
        );

    }

    @Override
    public void buildTransportControls() {
        Log.d(TAG, "buildTransportControls: called");
        List<AudioQueue> audioQueue = playerViewModel.getAudioQueueItems().getValue();
        if (audioQueue != null) {
            playerServiceManager.addQueueToSession(audioQueue);
        }
        btnPlayPause = (ImageView) getView().findViewById(R.id.audio_play_pause);
        btnStop = (ImageView) getView().findViewById(R.id.audio_stop);
        btnNext = (ImageView) getView().findViewById(R.id.audio_next);
        btnPrevious = (ImageView) getView().findViewById(R.id.audio_previous);
        bthShuffle = (ImageView) getView().findViewById(R.id.audio_shuffle);
        btnRepeat = (ImageView) getView().findViewById(R.id.audio_repeat);

        btnPlayPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        bthShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);

        mediaController = MediaControllerCompat.getMediaController(getActivity());

        MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                Log.d(TAG, "onPlaybackStateChanged: " + state.toString());
                super.onPlaybackStateChanged(state);
            }

            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                Log.d(TAG, "onMetadataChanged: " + metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM));
                super.onMetadataChanged(metadata);
            }
        };
        mediaController.registerCallback(controllerCallback);
    }

    @Override
    public void onClick(View v) {

        int playbackState = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getState();
        Log.d(TAG, "onClick: state: " + playbackState);
        switch (v.getId()) {
            case R.id.audio_play_pause:
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    switchPlayPauseButton("play");
                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().pause();
                } else if (playbackState == PlaybackStateCompat.STATE_PAUSED) {
                    Log.d(TAG, "onClick: Playback state paused");
                    switchPlayPauseButton("pause");
                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
                } else if (playbackState == PlaybackStateCompat.STATE_STOPPED) {
                    Log.d(TAG, "onClick: Playback state paused");
                    switchPlayPauseButton("pause");
                    MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
                }
                else {
                    Log.d(TAG, "onClick: Playback state Play");
                    switchPlayPauseButton("pause");
                    playFirstQueueItem();
                }
                break;
            case R.id.audio_stop:
                Log.d(TAG, "onClick: Playback stop");
                switchPlayPauseButton("play");
                playerManager.stopSeekbarSync();
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().stop();
                break;
            case R.id.audio_next:
                Log.d(TAG, "onClick: Playback next");
                switchPlayPauseButton("pause");
                playNextQueueItem();
                break;
            case R.id.audio_previous:
                Log.d(TAG, "onClick: Playback previous");
                switchPlayPauseButton("pause");
                playPreviousQueueItem();
                break;
        }
    }

    private void switchPlayPauseButton(String action) {
        if (action.equalsIgnoreCase("play")) {
            btnPlayPause.setImageResource(R.drawable.baseline_play_arrow_black_48);
        } else if (action.equalsIgnoreCase("pause")) {
            btnPlayPause.setImageResource(R.drawable.baseline_pause_black_48);
        }
    }
    private void playFirstQueueItem() {
        Log.d(TAG, "playFirstQueueItem: count ");
        Media firstQueueItem = MediaItemRepository.getMediaItem(getActivity().getApplication(),
                playerViewModel.getAudioQueueItems().getValue().get(queuePlayCount).getMediaId());
        playerManager.playMediaItem(firstQueueItem, true, 0);
    }

    private void playPreviousQueueItem() {
        Log.d(TAG, "playPreviousQueueItem:  QueueItem ");
        Long queueId = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getActiveQueueItemId();
        Log.d(TAG, "playPreviousQueueItem: id: " + queueId);
        if (queueId > -1) {
            MediaControllerCompat.getMediaController(getActivity()).getTransportControls().skipToPrevious();
            queueId--;
            String mediaId = MediaControllerCompat.getMediaController(getActivity()).getQueue().get(Integer.parseInt(queueId.toString())).getDescription().getMediaId();
            Media queueItem = MediaItemRepository.getMediaItem(getActivity().getApplication(), mediaId);
            playerManager.initPlay(queueItem);
            playerManager.startSeekbarSync();
            Log.d(TAG, "playPreviousQueueItem: QueueItem: " + queuePlayCount + " item: " + queueItem.getName());
        }

    }

    private void playNextQueueItem() {
        Log.d(TAG, "playNextQueueItem: count ");

        MediaControllerCompat.getMediaController(getActivity()).getTransportControls().skipToNext();
        Log.d(TAG, "playNextQueueItem: state: " + MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getActiveQueueItemId());
        Long queueId = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState().getActiveQueueItemId() + 1;
        String mediaId = MediaControllerCompat.getMediaController(getActivity()).getQueue().get(Integer.parseInt(queueId.toString())).getDescription().getMediaId();
        Media queueItem = MediaItemRepository.getMediaItem(getActivity().getApplication(), mediaId);
        playerManager.initPlay(queueItem);
        playerManager.startSeekbarSync();
        Log.d(TAG, "playNextQueueItem: queueId: " + queuePlayCount + " item: " + queueItem.getName());

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: start");
        super.onStart();
        playerServiceManager.start();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: start");
        super.onStop();
//        playerServiceManager.disconnect();
    }

    @Override
    public void onSingleTap(int position, MotionEvent event) {
        Log.d(TAG, "onSingleTap: called " + position);
        ImageView btnPlayPause = (ImageView) getView().findViewById(R.id.audio_play_pause);
        btnPlayPause.setImageResource(R.drawable.baseline_pause_black_48);
        queuePlayCount = position;
       playerManager.playMediaItem(playerViewModel.getMediaItemByAudioQueuePos(position), true, position);
    }

}
