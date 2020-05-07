package com.truvoice.medialibrary.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class PlainTextDialog extends DialogBuilder implements DialogBuilder.DialogListener {
    private static final String TAG = "AddPlaylistDialogFragme";

    private int layoutResource;
    private int titleStringResource;
    private int positiveButtonStringResource;
    private int negativeButtonStringResource;
    private PlainTextDialogListener callback;

    public interface PlainTextDialogListener {
        void OnDialogPlainTextPositiveClick(DialogFragment dialog, ArrayList selected);
        void OnDialogPlainTextNegativeClick(DialogFragment dialog);
    }
    public PlainTextDialog(Activity activity, PlainTextDialogListener callback) {
        super(activity);
        this.callback = callback;
        setListeners((DialogListener) this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        addView(getLayoutResource());
        addTitle(getTitleStringResource());
        addNegativeButton(getNegativeButtonStringResource());
        addPositiveButton(getPositiveButtonStringResource());
        return buildDialog();

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList selected) {
        callback.OnDialogPlainTextPositiveClick(dialog, selected);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogNegativeClick: clicked");
        callback.OnDialogPlainTextNegativeClick(dialog);
    }

    @Override
    public void onDialogListItemClick(DialogFragment dialog) {

    }

    public int getLayoutResource() {
        return layoutResource;
    }

    public void setLayoutResource(int layoutResource) {
        this.layoutResource = layoutResource;
    }

    public int getTitleStringResource() {
        return titleStringResource;
    }

    public void setTitleStringResource(int titleStringResource) {
        this.titleStringResource = titleStringResource;
    }

    public int getPositiveButtonStringResource() {
        return positiveButtonStringResource;
    }

    public void setPositiveButtonStringResource(int positiveButtonStringResource) {
        this.positiveButtonStringResource = positiveButtonStringResource;
    }

    public int getNegativeButtonStringResource() {
        return negativeButtonStringResource;
    }

    public void setNegativeButtonStringResource(int negativeButtonStringResource) {
        this.negativeButtonStringResource = negativeButtonStringResource;
    }
}
