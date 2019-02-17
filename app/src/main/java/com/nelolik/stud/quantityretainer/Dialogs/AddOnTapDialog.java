package com.nelolik.stud.quantityretainer.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nelolik.stud.quantityretainer.R;

public class AddOnTapDialog extends BottomSheetDialogFragment {

    public AddOnTapDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_bottom_sheet, container, false);

        return view;
    }
}
