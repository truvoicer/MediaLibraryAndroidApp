package com.truvoice.medialibrary.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_queue")
public class VideoQueue {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private String mediaId;
    private String uri;
    private String mediaType;
    private int sortOrder;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "_id=" + _id +
                ", mediaId='" + mediaId + '\'' +
                ", uri='" + uri + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
