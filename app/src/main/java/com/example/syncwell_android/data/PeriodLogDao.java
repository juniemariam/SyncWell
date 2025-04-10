package com.example.syncwell_android.data;

import androidx.room.*;

import java.util.List;

@Dao
public interface PeriodLogDao {

    @Insert
    void insert(PeriodLog log);

    @Query("SELECT * FROM PeriodLog ORDER BY startDate DESC")
    List<PeriodLog> getAllLogs();

    @Update
    void update(PeriodLog log);
}
