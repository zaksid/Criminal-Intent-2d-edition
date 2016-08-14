package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

/**
 * Holds TimePicker dialog
 */
public class TimePickerFragment extends DialogFragment {
    public final static String EXTRA_TIME = "com.zaksid.dev.android.training.bignerdranch.criminalintent.time";

    private final static String ARG_TIME = "date";

    private TimePicker timePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        return new AlertDialog.Builder(getActivity())
            .setView(view)
            .setTitle(R.string.time_picker_title)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}
