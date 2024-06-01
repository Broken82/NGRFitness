package com.example.ngrfitness.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "steps")
public class StepCount {

    @PrimaryKey
    public int uid;


    @ColumnInfo(name = "steps")
    public long steps;

    @ColumnInfo(name = "created_at")
    public String createdAt;
}
