package com.example.ngrfitness.Data;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Klasa reprezentująca encję `steps` w bazie danych Room.
 */
@Entity(tableName = "steps")
public class StepCount {

    @PrimaryKey(autoGenerate = true)
    public int uid; // Unikalny identyfikator rekordu

    @ColumnInfo(name = "steps")
    public long steps; // Liczba kroków

    @ColumnInfo(name = "created_at")
    public String createdAt; // Data utworzenia rekordu w formacie znaku czasu

    /**
     * Zwraca reprezentację obiektu StepCount w formie łańcucha znaków.
     *
     * @return String Reprezentacja obiektu StepCount.
     */
    @NonNull
    @Override
    public String toString() {
        LocalDate date = null;

        // Konwersja znaku czasu na lokalną datę
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                long timestamp = Long.parseLong(createdAt);
                Instant instant = Instant.ofEpochMilli(timestamp);
                date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Zwracanie sformatowanego ciągu znaków
        return String.format("Kroki: %d, Data: %s", steps, date != null ? date.toString() : "Błędna data");
    }
}
