package com.truvoice.medialibrary.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist_media")
public class PlaylistMedia {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    private int playlistId;
    private String mediaId;
    private String mediaSource;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(String mediaSource) {
        this.mediaSource = mediaSource;
    }

    @Override
    public String toString() {
        return "PlaylistMedia{" +
                "_id=" + _id +
                ", playlistId=" + playlistId +
                ", mediaId='" + mediaId + '\'' +
                ", mediaSource='" + mediaSource + '\'' +
                '}';
    }
}
