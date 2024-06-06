package com.example.ngrfitness.Data;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "steps")
public class StepCount {

    @PrimaryKey(autoGenerate = true)
    public int uid;


    @ColumnInfo(name = "steps")
    public long steps;

    @ColumnInfo(name = "created_at")
    public String createdAt;

    @NonNull
    @Override
    public String toString() {
        LocalDate date = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                long timestamp = Long.parseLong(createdAt);
                Instant instant = Instant.ofEpochMilli(timestamp);
                date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return String.format("Kroki: %d, Data: %s", steps, date != null ? date.toString() : "Błędna data");
    }
}
