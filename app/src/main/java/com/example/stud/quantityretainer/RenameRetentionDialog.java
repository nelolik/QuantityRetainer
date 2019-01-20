package com.example.stud.quantityretainer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

public class RenameRetentionDialog extends DialogFragment {

    private String mNewRetentionName;
    private RenameRetentionONClickListener mOnClickListener;
    private String mOldName;
    private EditText mText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mText = getDialog().findViewById(R.id.new_name);
        mText.setText(mOldName);
//        text.selectAll();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layoutInflater.inflate(R.layout.fragment_rename_dialog, null))
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

    public void setOldName(String mOldName) {
        this.mOldName = mOldName;
    }

    public interface RenameRetentionONClickListener {
        public void onDialogClickRename(String newName);
    }

    public void setOnClickListener(RenameRetentionONClickListener listener) {
        mOnClickListener = listener;
    }
}
