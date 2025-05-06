package com.example.syncwell_android;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
    private final List<PeriodLog> logs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cycle History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back arrow
        }

        recyclerView = findViewById(R.id.cycleRecyclerView);
        averageText = findViewById(R.id.averageCycleText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CycleAdapter(logs, new CycleAdapter.OnLogActionListener() {
            @Override
            public void onEdit(PeriodLog log) {
                if (!isFinishing() && !isDestroyed()) {
                    EditLogDialogFragment dialog = EditLogDialogFragment.newInstance(log, CycleHistoryActivity.this::loadLogs);
                    dialog.show(getSupportFragmentManager(), "edit_log");
                }
            }

            @Override
            public void onDelete(PeriodLog log) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getInstance(getApplicationContext()).periodLogDao().delete(log);
                    runOnUiThread(() -> {
                        Toast.makeText(CycleHistoryActivity.this, "Log deleted", Toast.LENGTH_SHORT).show();
                        loadLogs(); // refresh UI
                    });
                });
            }
        });

        recyclerView.setAdapter(adapter);
        loadLogs();
    }

    private void loadLogs() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<PeriodLog> newLogs = AppDatabase.getInstance(this).periodLogDao().getAllLogs();
            Collections.sort(newLogs, (a, b) -> Long.compare(b.startDate, a.startDate)); // latest first

            List<Integer> cycleLengths = new ArrayList<>();
            for (int i = 0; i < newLogs.size() - 1; i++) {
                PeriodLog current = newLogs.get(i);
                PeriodLog previous = newLogs.get(i + 1);
                int days = (int) ((current.startDate - previous.startDate) / (1000 * 60 * 60 * 24));
                cycleLengths.add(days);
            }

            int average = 0;
            if (!cycleLengths.isEmpty()) {
                average = (int) cycleLengths.stream().mapToInt(Integer::intValue).average().orElse(0);
            }

            int finalAverage = average;
            runOnUiThread(() -> {
                averageText.setText("Your average cycle length: " + finalAverage + " days");
                logs.clear();
                logs.addAll(newLogs);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Back button
        return true;
    }
}
