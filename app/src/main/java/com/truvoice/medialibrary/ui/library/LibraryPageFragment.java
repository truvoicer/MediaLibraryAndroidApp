package com.truvoice.medialibrary.ui.library;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.Selection;

import com.google.android.material.snackbar.Snackbar;
import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.entities.Playlist;
import com.truvoice.medialibrary.entities.PlaylistMedia;
import com.truvoice.medialibrary.entities.VideoQueue;
import com.truvoice.medialibrary.enums.DialogSource;
import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;
import com.truvoice.medialibrary.libraries.PlayerServiceManager;
import com.truvoice.medialibrary.recycleviews.RecycleViewBuilder;
import com.truvoice.medialibrary.repositories.PlaylistRepository;
import com.truvoice.medialibrary.ui.dialogues.ItemListDialog;
import com.truvoice.medialibrary.ui.dialogues.PlainTextDialog;
import com.truvoice.medialibrary.ui.fab.FabBuilder;
import com.truvoice.medialibrary.viewmodels.LibraryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryPageFragment extends Fragment implements LibraryActionModeCallback.LibraryActionModeListeners,
        OnItemActivatedListener, PlayerServiceManager.PlayerServiceCallbacks,
        PlainTextDialog.PlainTextDialogListener, FabBuilder.SnackbarOnClickListener,
        RecycleViewBuilder.PopupMenuListeners, PopupMenu.OnMenuItemClickListener,
        ItemListDialog.ItemClickDialogListener, PlaylistRepository.RepositoryCursorListener {

    private static final String TAG = "LibraryPageFragment";

    public static final String PAGE_ADAPTER_POSITION = "PAGE_ADAPTER_POSITION";

    private static final String RECYCLE_SELECTION_ID = "library_recycle_selection";
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private LibraryViewModel libraryViewModel;
    private MediaLibraryManager mediaLibraryManager;
    private PlayerServiceManager playerServiceManager;
    private DialogSource dialogSource;

    private List actionSelectedList;

    private RecycleViewBuilder recycleViewBuilder;
    
    private Media selectedMenuItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    init();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(getView(), R.string.request_permissions_denied, Snackbar.LENGTH_LONG).show();
                }
                return;

        }
    }
    private void init() {
        mediaLibraryManager = new MediaLibraryManager();
        libraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
        mediaLibraryManager.setCurrentTabSection(mediaLibraryManager.getTabSectionObject(getArguments().getInt(PAGE_ADAPTER_POSITION)));

        recycleViewBuilder = new RecycleViewBuilder(
                getView(),
                getContext(),
                R.id.library_list_recycler_view,
                mediaLibraryManager,
                this
        );
        recycleViewBuilder.buildMediaAdapter();
        setupFloatingActionBar();
        addMediaObserver();
        addPlaylistObserver();
        recycleViewBuilder.buildSelectionTracker(getActivity(),this,this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerServiceManager = new PlayerServiceManager(
                getView(), getActivity(), getContext(), this
        );
    }


    private void setupFloatingActionBar() {
        if (mediaLibraryManager.getCurrentTabSection().getCategory() == LibraryMediaCategory.PLAYLISTS) {
            FabBuilder fabBuilder = new FabBuilder(getView(), R.id.fab_library,
                    R.string.snackbar_add_playlist, Snackbar.LENGTH_LONG, R.string.snackbar_add_playlist, this);
        }
        else {
            FabBuilder fabBuilder = new FabBuilder(getView(), R.id.fab_library,
                    R.string.snackbar_add_media, Snackbar.LENGTH_LONG, R.string.snackbar_add_media, this);
        }
    }
    private void addMediaObserver() {
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        libraryViewModel.updateMediaItems(mediaLibraryManager.getCurrentTabSection().getMediaType(),
                mediaLibraryManager.getCurrentTabSection().getCategory(), sortOrder).observe(getViewLifecycleOwner(), new Observer<List<Media>>() {
            @Override
            public void onChanged(List<Media> mediaList) {
                if (mediaLibraryManager.getCurrentTabSection().getCategory() != LibraryMediaCategory.PLAYLISTS) {
//                    Log.d(TAG, "onChanged: ml: " + mediaList);
                    recycleViewBuilder.updateAdapterData(mediaList);
                }

            }
        });
    }


    private void addPlaylistObserver() {
        libraryViewModel.updateAllPlaylistsMediaRefs().observe(getViewLifecycleOwner(), new Observer<List<PlaylistMedia>>() {
            @Override
            public void onChanged(List<PlaylistMedia> playlistMediaList) {
                Log.d(TAG, "onChanged: observe call" + playlistMediaList.toString());
            }
        });
        libraryViewModel.updateAllPlaylists().observe(getViewLifecycleOwner(), new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                if (mediaLibraryManager.getCurrentTabSection().getCategory() == LibraryMediaCategory.PLAYLISTS) {
                    recycleViewBuilder.updateAdapterData(playlists);
                }
            }
        });
    }

    private void showAddPlaylistDialog() {
        PlainTextDialog newFragment = new PlainTextDialog(getActivity(), this);
        newFragment.setLayoutResource(R.layout.add_playlist_dialogue);
        newFragment.setTitleStringResource(R.string.add_playlist_dialogue_title);
        newFragment.setNegativeButtonStringResource(R.string.add_playlist_dialogue_cancel_button);
        newFragment.setPositiveButtonStringResource(R.string.add_playlist_dialogue_add_button);
        newFragment.show(getParentFragmentManager(), "add_playlist");
    }

    @Override
    public void OnActionModeAddToQueue(Selection selection) {
        List itemsList = MediaLibraryManager.iteratorToList(selection.iterator());
        Log.d(TAG, "onAddToQueueSelect: " + itemsList);
        if (recycleViewBuilder.getMediaAdapter().getInCategory()) {
            for (Object itemObject: itemsList) {
                if (itemObject instanceof Media) {
                    insertItemsToQueue(getCategoryItemList((Media) itemObject));
                }
                else if (itemObject instanceof Playlist) {
                    Playlist playlist = (Playlist) itemObject;
                    insertItemsToQueue(libraryViewModel.buildPlaylistMediaItems(
                            libraryViewModel.playlistMediaRefs.getValue(),
                            playlist.get_id()));
                }
            }
        } else {
            insertItemsToQueue(itemsList);
        }
    }

    private void insertItemsToQueue(List items) {
        Log.d(TAG, "insertItemsToQueue: Items: " + items.toString());
        int i = 0;
        for (Object itemObject: items) {
            if (itemObject instanceof Media) {
                Media mediaItem = (Media) itemObject;
                if (mediaItem.getMediaType().equalsIgnoreCase("audio")) {
                    AudioQueue queueItem = new AudioQueue();
                    queueItem.setMediaId(mediaItem.getMediaId());
                    queueItem.setMediaType(mediaItem.getMediaType());
                    queueItem.setSortOrder(i++);
                    libraryViewModel.addItemToAudioQueue(queueItem);
                } else if (mediaItem.getMediaType().equalsIgnoreCase("video")) {
                    VideoQueue queueItem = new VideoQueue();
                    queueItem.setMediaId(mediaItem.getMediaId());
                    queueItem.setMediaType(mediaItem.getMediaType());
                    queueItem.setSortOrder(i++);
                    libraryViewModel.addItemToVideoQueue(queueItem);
                } else {
                    throw new IllegalArgumentException("Media type in selection not audio or video.");
                }
            }
        }
    }

    private void playMediaItem(Media mediaItem) {
        Bundle playBundle = new Bundle();
        playBundle.putSerializable(PlayerServiceManager.PLAY_BUNDLE_MEDIA_ITEM, mediaItem);
        playBundle.putBoolean(PlayerServiceManager.PLAY_BUNDLE_IS_QUEUE_ITEM, false);
        playBundle.putInt(PlayerServiceManager.PLAY_BUNDLE_QUEUE_POS, 0);
        MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()))
                .getTransportControls()
                .playFromMediaId(mediaItem.getMediaId(), playBundle);
        MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromMediaId(mediaItem.getMediaId(), playBundle);
    }
    @Override
    public void OnActionModePlayItem(Selection selection) {
        Log.d(TAG, "onPlayItemSelect: " + selection);
        List<Media> mediaItemList = (List<Media>)  MediaLibraryManager.iteratorToList(selection.iterator());
        int i = 0;
        for (Media mediaItem:mediaItemList) {
            if (i == 0) {
                playMediaItem(mediaItem);
            }
            i++;
        }
        insertItemsToQueue(mediaItemList);
    }

    @Override
    public void OnActionModeDeleteItem(Selection selection) {
        Log.d(TAG, "onDeleteItemSelect: " + selection);
    }

    @Override
    public void OnActionModeAddToPlaylist(Selection selection) {
        actionSelectedList = MediaLibraryManager.iteratorToList(selection.iterator());
        dialogSource = DialogSource.SELECTION_TRACKER;
        if (recycleViewBuilder.getMediaAdapter().getInCategory()) {
            dialogSource = DialogSource.IN_CATEGORY;
        }
        showAddToPlaylistDialog();
    }

    @Override
    public void OnFabActionClick(View view) {
        if (mediaLibraryManager.getCurrentTabSection().getCategory() == LibraryMediaCategory.PLAYLISTS) {
            showAddPlaylistDialog();
        }
    }

    @Override
    public void OnPopupClick(View view, Object item) {
        Log.d(TAG, "onPopupClick: item: " + item.toString());
        if (item instanceof Media) {
            selectedMenuItem = (Media) item;
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.library_action_menu);
            popupMenu.setGravity(Gravity.RIGHT);
            popupMenu.show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_play_song:
                Log.d(TAG, "onMenuItemClick: play");
                playMediaItem(selectedMenuItem);
                break;
            case R.id.mnu_add_to_queue:
                Log.d(TAG, "onMenuItemClick: add to queue");
                break;
            case R.id.mnu_add_to_playlist:
                dialogSource = DialogSource.POPUP_MENU;
                showAddToPlaylistDialog();
                break;
        }
        return false;
    }

    private void showAddToPlaylistDialog() {
        libraryViewModel.getAllPlaylistsCursor(this);
    }

    @Override
    public void OnDialogListCursorFetch(Cursor cursor) {
        Log.d(TAG, "onCursor: " + cursor);
        if (cursor != null) {
            ItemListDialog newFragment = new ItemListDialog(getActivity(), this);
            newFragment.setTitleStringResource(R.string.add_playlist_dialogue_title);
            newFragment.setItemsCursor(cursor, "name", "_id");
            newFragment.setNegativeButtonStringResource(R.string.add_playlist_dialogue_cancel_button);
            newFragment.setPositiveButtonStringResource(R.string.add_playlist_dialogue_add_button);
            newFragment.show(getParentFragmentManager(), "add_to_playlist");
        }
    }

    @Override
    public void OnDialogItemListPositiveClick(DialogFragment dialog, ArrayList selected) {
        Log.d(TAG, "OnDialogItemListPositiveClick: called: " + selected);
        if (selected != null && selected.size() > 0) {
            for (Object playlistId: selected) {
                PlaylistMedia playlistMedia;
                switch (dialogSource) {
                    case POPUP_MENU:
                        playlistMedia = new PlaylistMedia();
                        playlistMedia.setMediaId(selectedMenuItem.getMediaId());
                        playlistMedia.setPlaylistId(Integer.parseInt(playlistId.toString()));
                        playlistMedia.setMediaSource(selectedMenuItem.getMediaSource().toString());
                        libraryViewModel.insertPlaylistMediaRef(playlistMedia);
                        break;
                    case SELECTION_TRACKER:
                    case IN_CATEGORY:
                        for (Object listObject : actionSelectedList) {
                            if (listObject instanceof Media) {
                                Log.d(TAG, "OnDialogItemListPositiveClick: Media");
                                insertItemsToPlaylist(getCategoryItemList((Media) listObject),
                                        Integer.parseInt(playlistId.toString()));
                            }
                        }
                        break;
                }
            }
        }
    }

    private void insertItemsToPlaylist(List itemList, int playlistId) {
        for (Object itemObject: itemList) {
            PlaylistMedia playlistMedia;
            if (itemObject instanceof Media) {
                Media mediaItem = (Media) itemObject;
                playlistMedia = new PlaylistMedia();
                playlistMedia.setMediaId(mediaItem.getMediaId());
                playlistMedia.setPlaylistId(playlistId);
                playlistMedia.setMediaSource(mediaItem.getMediaSource().toString());
                libraryViewModel.insertPlaylistMediaRef(playlistMedia);
            }
        }
    }
    @Override
    public void OnDialogPlainTextPositiveClick(DialogFragment dialog, ArrayList selected) {
        EditText addPlaylistText = (EditText) dialog.getDialog().findViewById(R.id.add_playlist_text);
        if (addPlaylistText != null) {
            if (!addPlaylistText.getText().toString().equals("")) {
                Playlist newPlaylist = new Playlist();
                newPlaylist.setName(addPlaylistText.getText().toString());
                libraryViewModel.insertNewPlaylist(newPlaylist);
            }
        }
    }

    @Override
    public void OnDialogPlainTextNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "onPlainTextDialogNegativeClick: called");
        dialog.getDialog().cancel();
    }

    @Override
    public void OnDialogItemListNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "OnDialogItemListNegativeClick: called");
    }


    private List<Media> getCategoryItemList(Media mediaItem) {
        Log.d(TAG, "onItemActivated: media item: " + mediaItem);
        String selection = null;
        String[] selectionArgs = null;
        switch (mediaLibraryManager.getCurrentTabSection().getCategory()) {
            case ALBUMS:
                selection = MediaStore.Audio.Media.ALBUM + " = ?";
                selectionArgs = new String[] { mediaItem.getAlbum() };
                break;
            case ARTISTS:
                selection = MediaStore.Audio.Media.ARTIST + " = ?";
                selectionArgs = new String[] { mediaItem.getArtist() };
                break;
        }

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        return libraryViewModel.getMediaItemQuery(
                mediaLibraryManager.getCurrentTabSection().getMediaType(),
                LibraryMediaCategory.ALL,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails item, @NonNull MotionEvent e) {
        Log.d(TAG, "onItemActivated: start"+ item);
        List<Media> mediaList = null;
        if (mediaLibraryManager.getCurrentTabSection().getCategory() == LibraryMediaCategory.PLAYLISTS &&
                recycleViewBuilder.getMediaAdapter().getInCategory()) {
            Playlist playlist = (Playlist) item.getSelectionKey();
            recycleViewBuilder.getMediaAdapter().setInCategory(false);
            mediaList = libraryViewModel.buildPlaylistMediaItems(
                    libraryViewModel.getAllPlaylistsMediaRefs().getValue(),
                    playlist.get_id());
            recycleViewBuilder.updateAdapterData(mediaList);
        } else {
            Media mediaItem = (Media) item.getSelectionKey();
            if(!recycleViewBuilder.getMediaAdapter().getInCategory()) {
                playMediaItem(mediaItem);
                return false;
            }
            mediaList = getCategoryItemList(mediaItem);
        }
        recycleViewBuilder.updateAdapterData(mediaList);
        recycleViewBuilder.getMediaAdapter().setInCategory(false);
        recycleViewBuilder.showBackButton();
        return false;
    }

    @Override
    public void OnListBackButtonClick() {
        recycleViewBuilder.getMediaAdapter().setCategory(mediaLibraryManager.getCurrentTabSection().getCategory());
        if (mediaLibraryManager.getCurrentTabSection().getCategory() == LibraryMediaCategory.PLAYLISTS) {
            recycleViewBuilder.updateAdapterData(libraryViewModel.getAllPlaylists().getValue());
        } else {
            String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
            List<Media> mediaList = libraryViewModel.updateMediaItems(
                    mediaLibraryManager.getCurrentTabSection().getMediaType(),
                    mediaLibraryManager.getCurrentTabSection().getCategory(),
                    sortOrder).getValue();
            recycleViewBuilder.updateAdapterData(mediaList);
        }
        recycleViewBuilder.hideBackButton();
    }


    @Override
    public void buildTransportControls() {

    }

    @Override
    public void onStart() {
        super.onStart();
        playerServiceManager.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
//        playerServiceManager.disconnect();
    }

}
