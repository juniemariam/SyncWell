package com.example.syncwell_android;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.syncwell_android.data.PeriodLog;

import java.text.SimpleDateFormat;
import java.util.*;

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.CycleViewHolder> {

    private List<PeriodLog> logs;
    private final OnLogClickListener listener;

    public interface OnLogClickListener {
        void onEdit(PeriodLog log);
    }

    public CycleAdapter(List<PeriodLog> logs, OnLogClickListener listener) {
        this.logs = logs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cycle_log, parent, false);
        return new CycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CycleViewHolder holder, int position) {
        PeriodLog log = logs.get(position);
        PeriodLog next = position + 1 < logs.size() ? logs.get(position + 1) : null;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String display = sdf.format(new Date(log.startDate)) + " - " + sdf.format(new Date(log.endDate));
        if (next != null) {
            int days = (int) ((log.startDate - next.startDate) / (1000 * 60 * 60 * 24));
            display += " | " + days + " days";
        }

        holder.textView.setText(display);
        holder.itemView.setOnClickListener(v -> listener.onEdit(log));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class CycleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CycleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cycleLogText);
        }
    }
}
