package com.nelolik.stud.quantityretainer.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.nelolik.stud.quantityretainer.R;

public class AddRetentionDialog extends DialogFragment {

    public interface AddRetentionOnCLick {
        void onDialogClickAdd(DialogFragment dialogFragment);
        void onDialogClickCancel(DialogFragment dialogFragment);
    }

    public static final String KEY_LISTENER_ACTIVITY = "listener_activity";

    private AddRetentionOnCLick mAddRetentionOnCLick;
    private String mNewRetentionName;

    public void setAddRetentionOnCLick(AddRetentionOnCLick mAddRetentionOnCLick) {
        this.mAddRetentionOnCLick = mAddRetentionOnCLick;
    }

    public String getNewRetentionName() {
        return mNewRetentionName;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layoutInflater.inflate(R.layout.fragment_add_dialog, null))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText text = getDialog().findViewById(R.id.new_retention_name);
                        mNewRetentionName = text.getText().toString();
                        mAddRetentionOnCLick.onDialogClickAdd(AddRetentionDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNewRetentionName = null;
                        mAddRetentionOnCLick.onDialogClickCancel(AddRetentionDialog.this);
                    }
                });
        return builder.create();
    }
}
