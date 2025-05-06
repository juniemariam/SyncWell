package com.example.syncwell_android;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.syncwell_android.PhaseUtils.PhaseDetails;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CycleFragment extends Fragment {

    private Calendar startDate;
    private Calendar endDate;
    private EditText cycleLengthInput;
    private TextView phaseOutput, dietTip, exerciseTip;
    private Button startDateButton, endDateButton, getPhaseButton;

    private final OkHttpClient client = new OkHttpClient();
    private final String backendUrl = "http://10.0.2.2:8000/query";  // Emulator only

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cycle, container, false);

        // UI references
        startDateButton = view.findViewById(R.id.startDateButton);
        endDateButton = view.findViewById(R.id.endDateButton);
        cycleLengthInput = view.findViewById(R.id.cycleLengthInput);
        getPhaseButton = view.findViewById(R.id.getPhaseButton);
        phaseOutput = view.findViewById(R.id.phaseOutput);
        dietTip = view.findViewById(R.id.dietTip);
        exerciseTip = view.findViewById(R.id.exerciseTip);

        // Date pickers
        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));

        // On click: Get current phase & call RAG
        getPhaseButton.setOnClickListener(v -> {
            if (startDate == null || cycleLengthInput.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int cycleLength = Integer.parseInt(cycleLengthInput.getText().toString());
            PhaseDetails details = PhaseUtils.calculatePhase(startDate, cycleLength);

            phaseOutput.setText("We feel you are in: " + details.phase + " phase");

            // Call RAG backend

            callRagModel("3 common foods to eat in " + details.phase.toLowerCase() + " phase.", true);
            callRagModel("Give me the top 3 recommended exercises to perform during the " + details.phase.toLowerCase() + " phase.", false);
        });

        return view;
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    if (isStartDate) {
                        startDate = selected;
                        startDateButton.setText("Start: " + sdf.format(selected.getTime()));
                    } else {
                        endDate = selected;
                        endDateButton.setText("End: " + sdf.format(selected.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void callRagModel(String question, boolean isDiet) {
        MediaType mediaType = MediaType.parse("application/json");
        Log.d("RAG_QUESTION_SENT", "Question being sent: " + question);
        String json = "{\"question\": \"" + question + "\"}";

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(backendUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RAG", "Request failed", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to reach server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respText = response.body().string();
                Log.d("RAG_RESPONSE", respText);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(respText);
                        String answer = jsonObject.getString("answer");

                        // Convert markdown-style bold (**) and line breaks into HTML
                        String styled = answer
                                .replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>")
                                .replace("\n", "<br>");

                        requireActivity().runOnUiThread(() -> {
                            if (isDiet) {
                                dietTip.setText(Html.fromHtml("<b>Diet Recommendations:</b><br>" + styled, Html.FROM_HTML_MODE_LEGACY));

//                                dietTip.setText("Diet Recommendations:\n" + answer);
                            } else {
                                exerciseTip.setText(Html.fromHtml("<b>Exercise Recommendations:</b><br>" + styled, Html.FROM_HTML_MODE_LEGACY));

//                                exerciseTip.setText("Exercise Recommendations:\n" + answer);
                            }
                        });

                    } catch (Exception e) {
                        Log.e("RAG", "Error parsing response", e);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error parsing server response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("RAG", "Server error: " + response.code());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
