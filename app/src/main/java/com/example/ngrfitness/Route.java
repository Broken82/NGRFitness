package com.example.ngrfitness;

/**
 * Klasa reprezentująca trasę w API Google Maps.
 */
public class Route {

    private OverviewPolyline overview_polyline; // Obiekt reprezentujący zakodowaną polilinię trasy

    /**
     * Zwraca obiekt OverviewPolyline, który zawiera zakodowane punkty trasy.
     *
     * @return OverviewPolyline Obiekt zawierający zakodowane punkty trasy.
     */
    public OverviewPolyline getOverviewPolyline() {
        return overview_polyline;
    }

    /**
     * Ustawia obiekt OverviewPolyline, który zawiera zakodowane punkty trasy.
     *
     * @param overviewPolyline Obiekt zawierający zakodowane punkty trasy.
     */
    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overview_polyline = overviewPolyline;
    }
}
