package com.arr.simple.model;

public class ItemsSlider {

    private int icon;
    private String title, bono, fecha;

    public ItemsSlider(int icon, String title, String bono, String fecha) {
        this.icon = icon;
        this.title = title;
        this.bono = bono;
        this.fecha = fecha;
    }

    public int getImage() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getBono() {
        return bono;
    }

    public String getFecha() {
        return fecha;
    }
}
