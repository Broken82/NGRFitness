package com.example.ngrfitness;

import java.util.List;

/**
 * Klasa reprezentująca wynik zapytania o trasy w API Google Maps.
 */
public class Result {
    private List<Route> routes; // Lista tras zwróconych przez API
    private String status; // Status odpowiedzi API

    /**
     * Zwraca listę tras.
     *
     * @return List<Route> Lista tras.
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     * Ustawia listę tras.
     *
     * @param routes Lista tras.
     */
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * Zwraca status odpowiedzi API.
     *
     * @return String Status odpowiedzi API.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Ustawia status odpowiedzi API.
     *
     * @param status Status odpowiedzi API.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
