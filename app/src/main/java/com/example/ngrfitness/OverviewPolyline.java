package com.example.ngrfitness;

/**
 * Klasa reprezentująca polilinię używaną w API Google Maps.
 */
public class OverviewPolyline {
    private String points; // Kodowane punkty polilinii

    /**
     * Zwraca zakodowane punkty polilinii.
     *
     * @return String zakodowane punkty polilinii.
     */
    public String getPoints() {
        return points;
    }

    /**
     * Ustawia zakodowane punkty polilinii.
     *
     * @param points Zakodowane punkty polilinii.
     */
    public void setPoints(String points) {
        this.points = points;
    }
}
