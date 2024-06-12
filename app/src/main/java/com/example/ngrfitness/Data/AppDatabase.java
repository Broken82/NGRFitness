package com.example.ngrfitness.Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Abstrakcyjna klasa bazy danych Room dla aplikacji.
 * Definiuje encje i wersję bazy danych oraz dostarcza DAO do zarządzania danymi kroków.
 */
@Database(entities = {StepCount.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Zwraca DAO (Data Access Object) do zarządzania danymi kroków.
     *
     * @return StepsDao DAO do zarządzania danymi kroków.
     */
    public abstract StepsDao stepsDao();
}
