package com.example.ngrfitness;

public class Picture {
    private int id;
    private String imageUlr;

    public Picture(int id, String imageUlr) {
        this.id = id;
        this.imageUlr = imageUlr;
    }
    public Picture(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUlr() {
        return imageUlr;
    }

    public void setImageUlr(String imageUlr) {
        this.imageUlr = imageUlr;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", imageUlr='" + imageUlr + '\'' +
                '}';
    }
}
