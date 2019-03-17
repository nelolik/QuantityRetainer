package com.nelolik.stud.quantityretainer.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.nelolik.stud.quantityretainer.R;

public class RenameRetentionDialog extends DialogFragment {

    private String mNewRetentionName;
    private RenameRetentionONClickListener mOnClickListener;
    private String mOldName;

    @SuppressWarnings("UnnecessaryLocalVariable")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_rename_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText text = getDialog().findViewById(R.id.new_name);
                        mNewRetentionName = text.getText().toString();
                        mOnClickListener.onDialogClickRename(mNewRetentionName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNewRetentionName = null;
//                        mAddRetentionOnCLick.onDialogClickCancel(AddRetentionDialog.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        EditText mText = getDialog().findViewById(R.id.new_name);
        mText.setText(mOldName);
        mText.selectAll();

    }

    public void setOldName(String mOldName) {
        this.mOldName = mOldName;
    }

    public interface RenameRetentionONClickListener {
        void onDialogClickRename(String newName);
    }

    public void setOnClickListener(RenameRetentionONClickListener listener) {
        mOnClickListener = listener;
    }
}
