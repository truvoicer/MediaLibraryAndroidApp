package com.truvoice.medialibrary.entities;

import com.truvoice.medialibrary.enums.LibraryMediaType;
import com.truvoice.medialibrary.enums.MediaSource;

import java.io.Serializable;

public class Media implements Serializable {
    private int id;
    private String mediaId;
    private String name;
    private String artist;
    private String album;
    private String size;
    private String duration;
    private String mimeType;
    private String dateAdded;
    private String year;
    private String mediaType;
    private MediaSource mediaSource;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setMediaType(LibraryMediaType mediaType) {
        this.mediaType = mediaType.toString();
    }

    public MediaSource getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    @Override
    public String toString() {
        return "Media{" +
                ", id=" + id +
                ", mediaId=" + mediaId +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", size='" + size + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                ", year='" + year + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", mediaType='" + mediaSource + '\'' +
                '}';
    }
}
