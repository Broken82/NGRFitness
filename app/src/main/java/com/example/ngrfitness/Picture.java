package com.example.ngrfitness;

/**
 * Klasa reprezentująca obraz z unikalnym identyfikatorem i adresem URL.
 */
public class Picture {
    private int id; // Unikalny identyfikator obrazu
    private String imageUlr; // Adres URL obrazu

    /**
     * Konstruktor tworzący obiekt Picture.
     *
     * @param id       Unikalny identyfikator obrazu.
     * @param imageUlr Adres URL obrazu.
     */
    public Picture(int id, String imageUlr) {
        this.id = id;
        this.imageUlr = imageUlr;
    }

    /**
     * Konstruktor tworzący obiekt Picture z samym identyfikatorem.
     *
     * @param id Unikalny identyfikator obrazu.
     */
    public Picture(int id) {
        this.id = id;
    }

    /**
     * Zwraca unikalny identyfikator obrazu.
     *
     * @return int Unikalny identyfikator obrazu.
     */
    public int getId() {
        return id;
    }

    /**
     * Ustawia unikalny identyfikator obrazu.
     *
     * @param id Unikalny identyfikator obrazu.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Zwraca adres URL obrazu.
     *
     * @return String Adres URL obrazu.
     */
    public String getImageUlr() {
        return imageUlr;
    }

    /**
     * Ustawia adres URL obrazu.
     *
     * @param imageUlr Adres URL obrazu.
     */
    public void setImageUlr(String imageUlr) {
        this.imageUlr = imageUlr;
    }

    /**
     * Zwraca reprezentację obiektu Picture w formie łańcucha znaków.
     *
     * @return String Reprezentacja obiektu Picture.
     */
    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", imageUlr='" + imageUlr + '\'' +
                '}';
    }
}
