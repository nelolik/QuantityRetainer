package com.example.stud.quantityretainer.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.stud.quantityretainer.R;

public class AddRetentionDialog extends DialogFragment {

    public interface AddRetentionOnCLick {
        public void onDialogClickAdd(DialogFragment dialogFragment);
        public void onDialogClickCancel(DialogFragment dialogFragment);
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

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     *
     * <p>This method will be called after {@link #onCreate(Bundle)} and
     * before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.  The
     * default implementation simply instantiates and returns a {@link Dialog}
     * class.
     *
     * <p><em>Note: DialogFragment own the {@link Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(DialogInterface)}
     * and {@link #onDismiss(DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
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
