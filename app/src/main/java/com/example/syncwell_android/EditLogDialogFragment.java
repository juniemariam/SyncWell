package com.example.syncwell_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.syncwell_android.data.AppDatabase;
import com.example.syncwell_android.data.PeriodLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EditLogDialogFragment extends DialogFragment {

    private PeriodLog periodLog;
    private Runnable onSavedCallback;

    private static final String ARG_START_DATE = "start_date";
    private static final String ARG_END_DATE = "end_date";
    private static final String ARG_LOG_ID = "log_id";

    // Factory method to create the dialog and pass data & callback
    public static EditLogDialogFragment newInstance(PeriodLog log, Runnable onSavedCallback) {
        EditLogDialogFragment fragment = new EditLogDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_START_DATE, log.startDate);
        args.putLong(ARG_END_DATE, log.endDate);
        args.putInt(ARG_LOG_ID, log.id);
        fragment.setArguments(args);
        fragment.setOnSavedCallback(onSavedCallback);
        return fragment;
    }

    private void setOnSavedCallback(Runnable callback) {
        this.onSavedCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getActivity() == null || getArguments() == null) return super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_log, null);

        EditText startInput = view.findViewById(R.id.startDateInput);
        EditText endInput = view.findViewById(R.id.endDateInput);

        long startMillis = getArguments().getLong(ARG_START_DATE);
        long endMillis = getArguments().getLong(ARG_END_DATE);
        int logId = getArguments().getInt(ARG_LOG_ID);

        periodLog = new PeriodLog(startMillis, endMillis);
        periodLog.id = logId;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        startInput.setText(sdf.format(new Date(periodLog.startDate)));
        endInput.setText(sdf.format(new Date(periodLog.endDate)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Period Log");
        builder.setView(view);
        builder.setPositiveButton("Save", (dialogInterface, which) -> {
            try {
                Date newStart = sdf.parse(startInput.getText().toString());
                Date newEnd = sdf.parse(endInput.getText().toString());

                if (newStart != null && newEnd != null) {
                    periodLog.startDate = newStart.getTime();
                    periodLog.endDate = newEnd.getTime();

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getInstance(requireContext())
                                .periodLogDao().update(periodLog);
                        if (onSavedCallback != null && getActivity() != null) {
                            requireActivity().runOnUiThread(onSavedCallback);
                        }
                    });

                    Toast.makeText(getContext(), "Period log updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }
}
