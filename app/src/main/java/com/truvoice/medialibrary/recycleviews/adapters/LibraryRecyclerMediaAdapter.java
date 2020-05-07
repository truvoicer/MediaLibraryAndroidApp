package com.truvoice.medialibrary.recycleviews.adapters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.entities.Playlist;
import com.truvoice.medialibrary.enums.LibraryMediaCategory;
import com.truvoice.medialibrary.libraries.MediaLibraryManager;
import com.truvoice.medialibrary.libraries.TabSection;

public class LibraryRecyclerMediaAdapter extends LibraryRecyclerAdapterBase {
    private static final String TAG = "LibraryRecyclerAdapter";

    private boolean inCategory = false;

    public LibraryRecyclerMediaAdapter(TabSection tabSection, LibraryRecyclerAdapterListeners listeners) {
        super(tabSection, listeners);
        this.tabSection = tabSection;
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryRecyclerAdapterBase.LibraryViewHolder holder, int position) {
        if (itemList != null && itemList.size() > 0) {
            try {
                if (selectionTracker != null) {
                    holder.bind(selectionTracker.isSelected(itemList.get(position)));
                }
                switch (tabSection.getCategory()) {
                    case ARTISTS:
                        showArtistItemDetails(holder, (Media) itemList.get(position));
                        break;
                    case ALBUMS:
                        showAlbumItemDetails(holder, (Media) itemList.get(position));
                        break;
                    case PLAYLISTS:
                        if (inCategory) {
                            showPlaylistItemDetails(holder, (Playlist) itemList.get(position));
                        } else {
                            showFullItemDetails(holder, (Media) itemList.get(position));
                        }
                    case ALL:
                    case VIDEOS:
                        showFullItemDetails(holder, (Media) itemList.get(position));
                        break;
                    default:
                        throw new IllegalArgumentException("Category is invalid.");
                }
            } catch (Exception e) {
                Log.d(TAG, "onBindViewHolder: Exception: " + e.getMessage());
            }
        }
        else {
            holder.mediaItemTitle.setText(R.string.empty_list_text);
        }
    }


    private void showFullItemDetails(@NonNull LibraryRecyclerAdapterBase.LibraryViewHolder holder, Media item) {
        Log.d(TAG, "showFullItemDetails: Fiile size: " + item.toString());
        holder.mediaItemTitle.setText(item.getName());
        holder.mediaItemDate.setText(
                MediaLibraryManager.getDate(item.getDateAdded()));
        holder.mediaItemSize.setText(
                MediaLibraryManager.formatFileSize("mb", Double.valueOf(item.getSize())));
    }

    private void showArtistItemDetails(@NonNull LibraryRecyclerAdapterBase.LibraryViewHolder holder, Media item) {
        if (inCategory) {
            holder.mediaItemTitle.setText(item.getArtist());
        } else {
            showFullItemDetails(holder, item);
        }
    }

    private void showAlbumItemDetails(@NonNull LibraryRecyclerAdapterBase.LibraryViewHolder holder, Media item) {
        if (inCategory) {
            holder.mediaItemTitle.setText(item.getAlbum());
        } else {
            showFullItemDetails(holder, item);
        }
    }
    private void showPlaylistItemDetails(@NonNull LibraryRecyclerAdapterBase.LibraryViewHolder holder, Playlist item) {
        if (inCategory) {
            holder.mediaItemTitle.setText(item.getName());
        } else {
            Object itemObj = (Object) item;
            showFullItemDetails(holder, (Media) itemObj);
        }
    }

    @Override
    public int getItemCount() {
        return (itemList == null || itemList.size() == 0) ? 1 : itemList.size();
    }

    public boolean getInCategory() {
        return this.inCategory;
    }

    public void setInCategory(boolean inCategory) {
        this.inCategory = inCategory;
    }

    public void setCategory(LibraryMediaCategory category) {
        switch (category) {
            case PLAYLISTS:
            case ALBUMS:
            case ARTISTS:
                this.inCategory = true;
                break;
            case ALL:
            case VIDEOS:
                this.inCategory = false;
                break;
        }
    }
}
