package com.example.ngrfitness;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Bazowa klasa dla aktywności w aplikacji, rozszerzająca AppCompatActivity.
 * Obsługuje aktualizację zasobów językowych przy tworzeniu aktywności.
 */
public class AppCompat extends AppCompatActivity {

    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     *
     * @param savedInstanceState Zapisany stan instancji.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja menedżera języków
        LenguageManager lenguageManager = new LenguageManager(this);

        // Aktualizacja zasobów językowych
        lenguageManager.updateResource(lenguageManager.getLang());
    }
}
