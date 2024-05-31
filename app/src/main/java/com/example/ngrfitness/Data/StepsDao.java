package com.example.ngrfitness.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StepsDao {

    //wszystkie
    @Query("SELECT * FROM steps")
    List<StepCount> getAll();


    //najwieksza
    @Query("SELECT MAX(steps) FROM steps")
    long getMax();

    //wstaw
    @Insert
    void insertAll(StepCount... stepCounts);

    //usun
    @Delete
    void delete(StepCount stepCount);
}
