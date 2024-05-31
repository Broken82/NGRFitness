package com.example.ngrfitness.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StepCount.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StepsDao stepsDao();
}
