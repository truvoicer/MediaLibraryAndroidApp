package com.truvoice.medialibrary.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;


public class DialogBuilder extends DialogFragment {
    private static final String TAG = "DialogBase";
    private DialogListener callback;
    private Activity activity;
    protected AlertDialog.Builder dialogBuilder;

    private ArrayList selectedItems;
    private ArrayList cursorItems;


    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, ArrayList selected);

        public void onDialogNegativeClick(DialogFragment dialog);

        public void onDialogListItemClick(DialogFragment dialog);

    }

    DialogBuilder(Activity activity) {
        this.activity = activity;
        dialogBuilder = new AlertDialog.Builder(activity);
    }

    void setListeners(DialogListener callback) {
        this.callback = callback;
    }

    void addView(int layoutResource) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogBuilder.setView(inflater.inflate(layoutResource, null));
    }

    void addTitle(int titleStringResource) {
        dialogBuilder.setTitle(titleStringResource);
    }

    void addNegativeButton(int negativeButtonStringResource) {
        dialogBuilder.setNegativeButton(negativeButtonStringResource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: clicked");
                callback.onDialogNegativeClick(DialogBuilder.this);
            }
        });
    }

    void addPositiveButton(int positiveButtonStringResource) {
        dialogBuilder.setPositiveButton(positiveButtonStringResource, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: clicked");
                callback.onDialogPositiveClick(DialogBuilder.this, selectedItems);
            }
        });
    }

    public void addList(CharSequence[] itemsArray) {
        dialogBuilder.setItems(itemsArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.onDialogListItemClick(DialogBuilder.this);
            }
        });
    }

    public void addMultiChoiceItemsCursor(Cursor itemsCursor, String idColumnName, String labelColumn) {
        cursorItems  = new ArrayList();
        selectedItems  = new ArrayList();
        if (itemsCursor != null) {
            while (itemsCursor.moveToNext()) {
                cursorItems.add(itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow(idColumnName)));
            }
            itemsCursor.moveToFirst();
            dialogBuilder.setMultiChoiceItems(itemsCursor, labelColumn, labelColumn, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        selectedItems.add(cursorItems.get(which));
                    } else if (selectedItems.contains(cursorItems.get(which))) {
                        // Else, if the item is already in the array, remove it
                        selectedItems.remove(cursorItems.get(Integer.valueOf(which)));
                    }
                }
            });
        }
    }

    public void addSingleChoiceItems(CharSequence[] itemsArray, int checkedItem) {
        dialogBuilder.setSingleChoiceItems(itemsArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItems  = new ArrayList();
                selectedItems.add(which);
            }
        });
    }

    Dialog buildDialog() {
        return dialogBuilder.create();
    }
}
