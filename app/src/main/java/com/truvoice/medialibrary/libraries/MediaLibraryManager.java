package com.truvoice.medialibrary.libraries;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.enums.LibraryMediaType;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class MediaLibraryManager implements Serializable {
    private static final String TAG = "LibraryManager";

    private List<TabSection> tabSections;
    private TabSection currentTabSection;
    private Application application;

    public MediaLibraryManager() {
        addTabSections();
    }

    private void addTabSections() {
        tabSections = new ArrayList<>();
        tabSections.add(new TabSection(0, LibraryMediaCategory.ALL, "All", LibraryMediaType.AUDIO));
        tabSections.add(new TabSection(1, LibraryMediaCategory.ARTISTS, "Artists", LibraryMediaType.AUDIO));
        tabSections.add(new TabSection(2, LibraryMediaCategory.ALBUMS, "Albums", LibraryMediaType.AUDIO));
        tabSections.add(new TabSection(3, LibraryMediaCategory.VIDEOS, "Videos", LibraryMediaType.VIDEO));
        tabSections.add(new TabSection(4, LibraryMediaCategory.PLAYLISTS, "Playlists", LibraryMediaType.AUDIO));
    }

    public TabSection getTabSectionObject(int position) {
        for (TabSection tabSection: tabSections) {
            int order = tabSection.getOrder();
            if (order == position) {
                return tabSection;
            }
        }
        throw new IllegalArgumentException("Can't find valid tab section");
    }

    public static Uri getContentUri(LibraryMediaType mediaType) {
        switch (mediaType) {
            case AUDIO:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            case VIDEO:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            default:
                throw new IllegalArgumentException("Media type is invalid.");
        }
    }

    public static String[] getProjection(LibraryMediaType mediaType) {
        String[] projection;
        switch (mediaType) {
            case AUDIO:
                return new String[] {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATE_ADDED,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                };
            case VIDEO:
                return new String[] {
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATE_ADDED,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.MIME_TYPE,
                };
            default:
                throw new IllegalStateException("Unexpected value: " + mediaType);
        }
    }

    public static String getMediaDuration(Context context, String mediaId, LibraryMediaType libraryMediaType) {
        Uri uri;
        if(libraryMediaType == LibraryMediaType.AUDIO) {
            uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Integer.parseInt(mediaId));

        } else if (libraryMediaType == LibraryMediaType.VIDEO) {
            uri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    Integer.parseInt(mediaId));

        } else {
            throw new IllegalArgumentException("Library media type isn't valid.");
        }
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mmr.release();
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedDuration(String duration) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(Double.parseDouble(duration) / 60000);
    }

    public static String getDate(String timestamp) {
        String pattern = "dd MMMM yyyy";
        SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
        formatDate.setTimeZone(TimeZone.getDefault());
        Date date = new Date(Long.parseLong(timestamp) * 1000);
        return formatDate.format(date);
    }

    public static String formatFileSize (String unit, Double size) {
        Double filesize = null;
        if (unit.equalsIgnoreCase("mb")) {
            filesize = size / Double.parseDouble("1048576");
            unit = "MB";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(filesize) + unit;
    }

    public List<TabSection> getTabSections() {
        return tabSections;
    }

    public TabSection getCurrentTabSection() {
        return currentTabSection;
    }

    public void setCurrentTabSection(TabSection currentTabSection) {
        this.currentTabSection = currentTabSection;
    }

    public static List iteratorToList(Iterator<Media> iterator) {
        List<Object> selectionList = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            selectionList.add(item);
        }
        return selectionList;
    }
}
