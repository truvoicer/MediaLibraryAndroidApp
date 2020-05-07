package com.truvoice.medialibrary.recycleviews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.libraries.TabSection;
import com.truvoice.medialibrary.recycleviews.LibraryItemDetail;

import java.util.ArrayList;
import java.util.List;

public class LibraryRecyclerAdapterBase extends RecyclerView.Adapter<LibraryRecyclerAdapterBase.LibraryViewHolder> {
    private static final String TAG = "LibraryRecyclerAdapter";

    protected TabSection tabSection;
    protected SelectionTracker selectionTracker;
    protected List itemList;
    protected LibraryRecyclerAdapterListeners listeners;



    public interface LibraryRecyclerAdapterListeners {
        void onPopupMenuClicked(View view, Object item);
    }
    public LibraryRecyclerAdapterBase(TabSection tabSection, LibraryRecyclerAdapterListeners listeners) {
//        Log.d(TAG, "LibraryRecyclerAdapter: start");
        this.tabSection = tabSection;
        this.listeners = listeners;
        itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_list_item, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public void updateData(List itemList) {
        this.itemList =  itemList;
        notifyDataSetChanged();
    }

    public List getData() {
        return this.itemList;
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaItemImage;
        TextView mediaItemTitle;
        TextView mediaItemDate;
        TextView mediaItemSize;
        ImageView popupButton;

        LibraryViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.mediaItemImage = (ImageView) itemView.findViewById(R.id.list_item_play);
            this.mediaItemTitle = (TextView) itemView.findViewById(R.id.list_item_title);
            this.mediaItemDate = (TextView) itemView.findViewById(R.id.list_item_date);
            this.mediaItemSize = (TextView) itemView.findViewById(R.id.list_item_duration);
            this.popupButton = (ImageView) itemView.findViewById(R.id.list_popup_button);
            View.OnClickListener popupListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listeners.onPopupMenuClicked(itemView, itemList.get(getAdapterPosition()));
                }
            };
            popupButton.setOnClickListener(popupListener);
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new LibraryItemDetail(getAdapterPosition(), itemList.get(getAdapterPosition()));
        }

        public final void bind(boolean isActive) {
            itemView.setActivated(isActive);
        }
    }
}
