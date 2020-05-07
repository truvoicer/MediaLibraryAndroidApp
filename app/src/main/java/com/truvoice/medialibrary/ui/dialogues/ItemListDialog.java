package com.truvoice.medialibrary.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ItemListDialog extends DialogBuilder implements DialogBuilder.DialogListener {
    private static final String TAG = "ItemListDialog";

    private CharSequence[] itemArray;
    private Cursor itemsCursor;
    private String labelColumnName;
    private String idColumnName;
    private int titleStringResource;
    private int positiveButtonStringResource;
    private int negativeButtonStringResource;
    private ItemClickDialogListener callback;

    public interface ItemClickDialogListener {
        public void OnDialogItemListPositiveClick(DialogFragment dialog, ArrayList selected);
        public void OnDialogItemListNegativeClick(DialogFragment dialog);
//        public void OnDialogItemListSelectedClick(DialogFragment dialog);
    }
    public ItemListDialog(Activity activity, ItemClickDialogListener callback) {
        super(activity);
        this.callback = callback;
        setListeners((DialogListener) this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        addTitle(getTitleStringResource());
        addMultiChoiceItemsCursor(getItemsCursor(), idColumnName, labelColumnName);
        addNegativeButton(getNegativeButtonStringResource());
        addPositiveButton(getPositiveButtonStringResource());
        return buildDialog();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList selected) {
        callback.OnDialogItemListPositiveClick(dialog, selected);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogNegativeClick: clicked");
        callback.OnDialogItemListNegativeClick(dialog);
    }

    @Override
    public void onDialogListItemClick(DialogFragment dialog) {
//        callback.OnDialogItemListSelectedClick(dialog);
    }

    public int getTitleStringResource() {
        return titleStringResource;
    }

    public void setTitleStringResource(int titleStringResource) {
        this.titleStringResource = titleStringResource;
    }

    public CharSequence[] getItemArray() {
        return itemArray;
    }

    public void setItemArray(CharSequence[] itemArray) {
        this.itemArray = itemArray;
    }

    public Cursor getItemsCursor() {
        return itemsCursor;
    }

    public void setItemsCursor(Cursor itemsCursor, String labelColumnName, String idColumnName) {
        this.itemsCursor = itemsCursor;
        this.labelColumnName = labelColumnName;
        this.idColumnName = idColumnName;
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
