package com.example.ngrfitness.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Interfejs DAO (Data Access Object) dla tabeli `steps` w bazie danych.
 * Definiuje metody do wykonywania operacji na danych kroków.
 */
@Dao
public interface StepsDao {

    /**
     * Pobiera wszystkie rekordy z tabeli `steps`.
     *
     * @return List<StepCount> Lista wszystkich rekordów z tabeli `steps`.
     */
    @Query("SELECT * FROM steps")
    List<StepCount> getAll();

    /**
     * Pobiera największą liczbę kroków z tabeli `steps`.
     *
     * @return long Największa liczba kroków.
     */
    @Query("SELECT MAX(steps) FROM steps")
    long getMax();

    /**
     * Wstawia rekordy do tabeli `steps`.
     *
     * @param stepCounts Rekordy do wstawienia.
     */
    @Insert
    void insertAll(StepCount... stepCounts);

    /**
     * Usuwa rekord z tabeli `steps`.
     *
     * @param stepCount Rekord do usunięcia.
     */
    @Delete
    void delete(StepCount stepCount);
}
