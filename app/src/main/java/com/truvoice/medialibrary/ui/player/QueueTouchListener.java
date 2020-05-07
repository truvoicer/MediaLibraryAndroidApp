package com.truvoice.medialibrary.ui.player;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class QueueTouchListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "QueueTouchListener";

    private Context context;
    private GestureDetectorCompat gestureDetectorCompat;
    private RecyclerView recyclerView;
    private OnQueueTouchEvents callback;

   interface OnQueueTouchEvents {
       void onSingleTap(int position, MotionEvent event);
   }
    public QueueTouchListener(final OnQueueTouchEvents callback, Context context, final RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: called");
                View childViewUnder = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int childPosition = recyclerView.getChildAdapterPosition(childViewUnder);
                callback.onSingleTap(childPosition, e);
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onContextClick(MotionEvent e) {
                Log.d(TAG, "onContextClick: called");
                return super.onContextClick(e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
