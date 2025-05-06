package com.example.syncwell_android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.*;
import android.widget.*;

import com.example.syncwell_android.data.AppDatabase;
import com.example.syncwell_android.data.PeriodLog;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private EditText nameInput, ageInput, weightInput, heightInput;
    private Button saveButton;
    private Button startDateButton, endDateButton, logPeriodButton, viewHistoryButton;
    private TextView logsView, bmiView, profileDisplay;

    private Calendar selectedStartDate, selectedEndDate;
    private SharedPreferences preferences;
    private boolean isEditing = false;
    private MenuItem editMenuItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        // Profile fields
        nameInput = view.findViewById(R.id.nameInput);
        ageInput = view.findViewById(R.id.ageInput);
        weightInput = view.findViewById(R.id.weightInput);
        heightInput = view.findViewById(R.id.heightInput);
        saveButton = view.findViewById(R.id.saveButton);
        profileDisplay = view.findViewById(R.id.profileDisplay);
        bmiView = view.findViewById(R.id.bmiTextView);

        preferences = requireActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);

        nameInput.setText(preferences.getString("name", ""));
        ageInput.setText(preferences.getString("age", ""));
        weightInput.setText(preferences.getString("weight", ""));
        heightInput.setText(preferences.getString("height", ""));

        saveButton.setOnClickListener(v -> saveUserInfo());

        // Initialize period buttons
        logPeriodButton = view.findViewById(R.id.btnLogPeriod);
        viewHistoryButton = view.findViewById(R.id.btnViewHistory);

        logPeriodButton.setOnClickListener(v -> openLogPeriodDialog());
        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CycleHistoryActivity.class);
            startActivity(intent);
        });

        updateUIForMode();
        return view;
    }

    private void openLogPeriodDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_log_period, null);
        Button startBtn = dialogView.findViewById(R.id.btnStartDate);
        Button endBtn = dialogView.findViewById(R.id.btnEndDate);
        Button logBtn = dialogView.findViewById(R.id.btnLog);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Log Period")
                .setView(dialogView)
                .create();

        Calendar localStartDate = Calendar.getInstance();
        Calendar localEndDate = Calendar.getInstance();

        startBtn.setOnClickListener(v -> showDatePickerDialog(localStartDate, date -> {
            selectedStartDate = date;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            startBtn.setText("Start: " + sdf.format(date.getTime()));
        }));

        endBtn.setOnClickListener(v -> showDatePickerDialog(localEndDate, date -> {
            selectedEndDate = date;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            endBtn.setText("End: " + sdf.format(date.getTime()));
        }));

        logBtn.setOnClickListener(v -> {
            if (selectedStartDate == null || selectedEndDate == null) {
                Toast.makeText(getContext(), "Please select both dates", Toast.LENGTH_SHORT).show();
            } else {
                savePeriodLog();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDatePickerDialog(Calendar initialDate, DatePickerListener listener) {
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    listener.onDateSelected(selected);
                },
                initialDate.get(Calendar.YEAR),                initialDate.get(Calendar.MONTH),

                initialDate.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    interface DatePickerListener {
        void onDateSelected(Calendar date);
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        editMenuItem = menu.findItem(R.id.menu_edit);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateUIForMode() {
        if (isEditing) {
            nameInput.setVisibility(View.VISIBLE);
            ageInput.setVisibility(View.VISIBLE);
            weightInput.setVisibility(View.VISIBLE);
            heightInput.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            profileDisplay.setVisibility(View.GONE);
            bmiView.setVisibility(View.GONE);
        } else {
            nameInput.setVisibility(View.GONE);
            ageInput.setVisibility(View.GONE);
            weightInput.setVisibility(View.GONE);
            heightInput.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            profileDisplay.setVisibility(View.VISIBLE);
            bmiView.setVisibility(View.VISIBLE);
            displayProfile();
        }
    }

    private void displayProfile() {
        String name = preferences.getString("name", "");
        String age = preferences.getString("age", "");
        String weight = preferences.getString("weight", "");
        String height = preferences.getString("height", "");

        profileDisplay.setText( "User Information"+ "\n\nName: " + name + "\nAge: " + age + "\nWeight: " + weight + "kg\nHeight: " + height + "cm");

        try {
            float w = Float.parseFloat(weight);
            float h = Float.parseFloat(height) / 100;
            float bmi = w / (h * h);
            bmiView.setText(String.format(Locale.getDefault(), "BMI: %.2f", bmi));
        } catch (Exception e) {
            bmiView.setText("BMI: N/A");
        }
    }

    private void saveUserInfo() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", nameInput.getText().toString());
        editor.putString("age", ageInput.getText().toString());
        editor.putString("weight", weightInput.getText().toString());
        editor.putString("height", heightInput.getText().toString());
        editor.apply();

        Toast.makeText(getContext(), "Profile saved!", Toast.LENGTH_SHORT).show();
        updateUIForMode();
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    if (isStart) {
                        selectedStartDate = selected;
                        startDateButton.setText("Start: " + sdf.format(selected.getTime()));
                    } else {
                        selectedEndDate = selected;
                        endDateButton.setText("End: " + sdf.format(selected.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void savePeriodLog() {
        if (selectedStartDate == null || selectedEndDate == null) {
            Toast.makeText(getContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        long startMillis = selectedStartDate.getTimeInMillis();
        long endMillis = selectedEndDate.getTimeInMillis();

        PeriodLog log = new PeriodLog(startMillis, endMillis);

        new Thread(() -> {
            AppDatabase.getInstance(requireContext()).periodLogDao().insert(log);
        }).start();

        Toast.makeText(getContext(), "Period logged!", Toast.LENGTH_SHORT).show();
        selectedStartDate = null;
        selectedEndDate = null;
    }

    private void showAllLogs() {
        AppDatabase db = AppDatabase.getInstance(requireContext());

        Executors.newSingleThreadExecutor().execute(() -> {
            List<PeriodLog> logs = db.periodLogDao().getAllLogs();

            StringBuilder logText = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            for (PeriodLog log : logs) {
                String start = sdf.format(new Date(log.startDate));
                String end = sdf.format(new Date(log.endDate));
                logText.append("Start: ").append(start).append("\n");
                logText.append("End: ").append(end).append("\n\n");
            }

            requireActivity().runOnUiThread(() -> logsView.setText(logText.toString()));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEditing) {
            displayProfile(); // Update UI after returning from EditProfileActivity
        }
    }

}
