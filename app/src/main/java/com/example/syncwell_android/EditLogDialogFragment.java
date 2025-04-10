package com.example.syncwell_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.syncwell_android.data.AppDatabase;
import com.example.syncwell_android.data.PeriodLog;

import java.util.Calendar;

public class EditLogDialogFragment extends DialogFragment {

    private static final String ARG_LOG = "log";
    private PeriodLog log;
    private Calendar startCal, endCal;

    public static EditLogDialogFragment newInstance(PeriodLog log) {
        EditLogDialogFragment fragment = new EditLogDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOG, log);
        fragment.setArguments(args);
        return fragment;
    }
    private final DatePickerDialog.OnDateSetListener endDateSetListener = (view, year, month, dayOfMonth) -> {
        endCal.set(year, month, dayOfMonth);
        log.startDate = startCal.getTimeInMillis();
        log.endDate = endCal.getTimeInMillis();

        new Thread(() -> {
            AppDatabase.getInstance(requireContext()).periodLogDao().update(log);
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Log updated!", Toast.LENGTH_SHORT).show()
            );
        }).start();
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        log = (PeriodLog) getArguments().getSerializable(ARG_LOG);
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.setTimeInMillis(log.startDate);
        endCal.setTimeInMillis(log.endDate);

        return new DatePickerDialog(requireContext(), startDateSetListener,
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH));
    }

    private final DatePickerDialog.OnDateSetListener startDateSetListener = (view, year, month, dayOfMonth) -> {
        startCal.set(year, month, dayOfMonth);
        new DatePickerDialog(requireContext(), endDateSetListener,
                endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH),
                endCal.get(Calendar.DAY_OF_MONTH)).show();
    };


}
