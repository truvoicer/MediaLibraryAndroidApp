package com.truvoice.medialibrary.recycleviews.adapters;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.truvoice.medialibrary.R;
import com.truvoice.medialibrary.entities.AudioQueue;
import com.truvoice.medialibrary.entities.Media;
import com.truvoice.medialibrary.repositories.MediaItemRepository;

import java.util.List;

public class QueueRecyclerAdapter extends RecyclerView.Adapter<QueueRecyclerAdapter.QueueRecyclerViewHolder> {
    private static final String TAG = "QueueRecyclerAdapter";
    private Context context;
    private List<AudioQueue> audioQueueItems;
    private Application application;

    public QueueRecyclerAdapter(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    public void updateQueueItems (List<AudioQueue> audioQueueItems) {
        this.audioQueueItems = audioQueueItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QueueRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_list_item, parent, false);
        return new QueueRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueRecyclerViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: audioQueueitems: " + audioQueueItems);
        if (audioQueueItems != null && audioQueueItems.size() > 0) {
            AudioQueue audioQueueItem = audioQueueItems.get(position);
            Log.d(TAG, "onBindViewHolder: " + audioQueueItem.getMediaId());
            Media getMedia = MediaItemRepository.getMediaItem(application, audioQueueItem.getMediaId());
            if(getMedia != null) {
                holder.queueItemTitle.setText(getMedia.getName());
            }
            else {
                holder.queueItemTitle.setText(R.string.audio_retrieve_error_text);
            }
        }
        else {
            holder.queueItemTitle.setText(R.string.empty_queue_text);
        }
    }

    @Override
    public int getItemCount() {
        return (audioQueueItems == null || audioQueueItems.size() == 0)? 1 : audioQueueItems.size();
    }

    public class QueueRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView queueItemPlay;
        TextView queueItemTitle;
        TextView queueItemDuration;

        QueueRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            queueItemPlay = itemView.findViewById(R.id.list_item_play);
            queueItemTitle = itemView.findViewById(R.id.list_item_title);
            queueItemDuration = itemView.findViewById(R.id.list_item_duration);
        }
    }
}
