package com.example.syncwell_android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.syncwell_android.data.AppDatabase;
import com.example.syncwell_android.data.PeriodLog;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class CycleHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CycleAdapter adapter;
    private TextView averageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_history);

        getSupportActionBar().setTitle("Cycle History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back arrow

        recyclerView = findViewById(R.id.cycleRecyclerView);
        averageText = findViewById(R.id.averageCycleText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<PeriodLog> logs = AppDatabase.getInstance(this).periodLogDao().getAllLogs();
            Collections.sort(logs, (a, b) -> Long.compare(b.startDate, a.startDate)); // latest first

            List<Integer> cycleLengths = new ArrayList<>();
            List<String> formattedLogs = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            for (int i = 0; i < logs.size(); i++) {
                PeriodLog current = logs.get(i);
                if (i + 1 < logs.size()) {
                    PeriodLog previous = logs.get(i + 1);
                    int days = (int) ((current.startDate - previous.startDate) / (1000 * 60 * 60 * 24));
                    cycleLengths.add(days);
                    formattedLogs.add(sdf.format(current.startDate) + " - " + sdf.format(current.endDate) + " | " + days + " days");
                } else {
                    formattedLogs.add(sdf.format(current.startDate) + " - " + sdf.format(current.endDate));
                }
            }

            int average = 0;
            if (!cycleLengths.isEmpty()) {
                average = (int) cycleLengths.stream().mapToInt(Integer::intValue).average().orElse(0);
            }

            int finalAverage = average;
            runOnUiThread(() -> {
                averageText.setText("Your average cycle length: " + finalAverage + " days");
                adapter = new CycleAdapter(logs, log -> {
                    EditLogDialogFragment dialog = EditLogDialogFragment.newInstance(log);
                    dialog.show(getSupportFragmentManager(), "edit_log");
                });
                recyclerView.setAdapter(adapter);
            });

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Back button
        return true;
    }
}
