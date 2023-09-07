package com.arr.simple.model;

import java.util.Objects;

public class Contact implements Items {

    String name;
    String number;
    String photo;
    boolean isFavorite;

    public Contact() {}

    public Contact(String name, String number, String photo, boolean isFavorite) {
        this.name = name;
        this.number = number;
        this.photo = photo;
        this.isFavorite = isFavorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int getViewType() {
        return Contact.VIEW_GRID;
    }
}
