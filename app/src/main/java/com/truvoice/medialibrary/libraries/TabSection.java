package com.truvoice.medialibrary.libraries;

import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.enums.LibraryMediaType;

import java.io.Serializable;

public class TabSection implements Serializable {
    private int order;
    private LibraryMediaCategory category;
    private String title;
    private LibraryMediaType mediaType;

    TabSection(int order, LibraryMediaCategory category, String title, LibraryMediaType mediaType) {
        this.order = order;
        this.category = category;
        this.title = title;
        this.mediaType = mediaType;
    }

    public int getOrder() {
        return order;
    }

    public LibraryMediaCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public LibraryMediaType getMediaType() {
        return mediaType;
    }
}