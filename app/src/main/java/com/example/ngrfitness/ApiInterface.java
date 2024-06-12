package com.example.ngrfitness;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfejs definiujący wywołania API do Google Maps Directions.
 */
public interface ApiInterface {

    /**
     * Pobiera kierunki pomiędzy dwoma punktami.
     *
     * @param mode       Tryb podróży (np. "driving", "walking").
     * @param preference Preferencja routingu tranzytowego (np. "less_driving").
     * @param origin     Punkt początkowy (współrzędne lub adres).
     * @param destination Punkt docelowy (współrzędne lub adres).
     * @param key        Klucz API Google Maps.
     * @return Single<Result> Wynik zapytania z API Google Maps Directions.
     */
    @GET("maps/api/directions/json")
    Single<Result> getDirection(@Query("mode") String mode,
                                @Query("transit routing preference") String preference,
                                @Query("origin") String origin,
                                @Query("destination") String destination,
                                @Query("key") String key);
}
