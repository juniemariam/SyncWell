package com.example.syncwell_android.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PeriodLog implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long startDate;
    public long endDate;

    public PeriodLog(long startDate, long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
