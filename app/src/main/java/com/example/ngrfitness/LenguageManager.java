package com.example.ngrfitness;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Klasa zarządzająca ustawieniami języka w aplikacji.
 */
public class LenguageManager {

    private Context ct; // Kontekst aplikacji
    private SharedPreferences sharedPreferences; // SharedPreferences do przechowywania ustawień języka

    /**
     * Konstruktor klasy LanguageManager.
     *
     * @param ctx Kontekst aplikacji.
     */
    public LenguageManager(Context ctx) {
        ct = ctx;
        sharedPreferences = ct.getSharedPreferences("LANG", Context.MODE_PRIVATE);
    }

    /**
     * Ustawia język aplikacji.
     *
     * @param code Kod języka (np. "en" dla angielskiego).
     */
    public void setLang(String code) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", code);
        editor.commit(); // Zapisuje zmiany do SharedPreferences
    }

    /**
     * Pobiera aktualnie ustawiony język.
     *
     * @return Kod języka (domyślnie "en").
     */
    public String getLang() {
        return sharedPreferences.getString("lang", "en");
    }

    /**
     * Aktualizuje zasoby aplikacji, aby używać nowego języka.
     *
     * @param code Kod języka (np. "en" dla angielskiego).
     */
    public void updateResource(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Resources resources = ct.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        setLang(code); // Zapisuje nowy język w SharedPreferences
    }
}
