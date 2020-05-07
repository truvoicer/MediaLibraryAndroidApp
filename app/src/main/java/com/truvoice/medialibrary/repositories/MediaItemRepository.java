package com.truvoice.medialibrary.repositories;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.enums.LibraryMediaType;
import com.truvoice.medialibrary.enums.MediaSource;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;

import java.util.ArrayList;
import java.util.List;

public class MediaItemRepository extends BaseRepository {
    private static final String TAG = "MediaItemRepository";
    private List<Media> mediaList;
    private ArrayList distinctList;

    public MediaItemRepository(Application application) {
        super(application);
    }

    public List<Media> queryMediaItems(LibraryMediaType mediaType, LibraryMediaCategory category,
                                       String selection, String[] selectionArgs, String sortOrder) {
        ContentResolver contentResolver = application.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaLibraryManager.getContentUri(mediaType),
                MediaLibraryManager.getProjection(mediaType),
                selection,
                selectionArgs,
                sortOrder
        );
        if (cursor != null) {
            mediaList = new ArrayList<>();
            distinctList = new ArrayList();

            while (cursor.moveToNext()) {
                Media media = new Media();
                if (mediaType == LibraryMediaType.AUDIO) {

                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                    if(checkCategoryCalled(category, artist, album)) {
                        continue;
                    }

                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    media.setMediaId(id);
                    media.setMediaType(LibraryMediaType.AUDIO);
                    media.setMediaSource(MediaSource.SYSTEM);
                    media.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    media.setArtist(artist);
                    media.setAlbum(album);
                    media.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                    media.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)));
                    media.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)));


                } else if (mediaType == LibraryMediaType.VIDEO) {
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    media.setMediaId(id);
                    media.setMediaType(LibraryMediaType.VIDEO);
                    media.setMediaSource(MediaSource.SYSTEM);
                    media.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                    media.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                    media.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                    media.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)));
                }
                mediaList.add(media);
                Log.d(TAG, "queryMediaItems: arraylist: " + distinctList.toString());
            }
        }
//        Log.d(TAG, "queryMediaItems: MediaList = " + mediaList.toString());
        return mediaList;
    }


    private boolean checkCategoryCalled(LibraryMediaCategory category, String artist, String album) {
        Boolean hasBeenCalled = false;
        if (category == LibraryMediaCategory.ARTISTS && distinctList.size() > 0 &&
                distinctList.contains(artist)) {
            hasBeenCalled = true;
        }
        else if (category == LibraryMediaCategory.ALBUMS && distinctList.size() > 0 &&
                distinctList.contains(album)) {
            hasBeenCalled = true;
        }
        switch (category) {
            case ARTISTS:
                distinctList.add(artist);
            case ALBUMS:
                distinctList.add(album);
        }
        return hasBeenCalled;
    }

    public static Media getMediaItem(Application application, String mediaId) {
        Log.d(TAG, "getMediaItem: called");
        ContentResolver resolver = application.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATE_ADDED},
                MediaStore.Audio.Media._ID + " = " + mediaId,
                null,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.d(TAG, "getMediaItem: called cursor");
                Media media = new Media();
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                media.setMediaId(id);
                media.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                media.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                media.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                media.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                media.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)));
                media.setMediaType(LibraryMediaType.AUDIO);
                return media;
            }
            cursor.close();
        }
        return null;
    }

}
