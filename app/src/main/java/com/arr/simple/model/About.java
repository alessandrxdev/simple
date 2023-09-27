package com.arr.simple.model;

public class About implements Items {

    private String name, descrip;
    private int icon;

    public About(int icon, String name, String descrip) {
        this.icon = icon;
        this.name = name;
        this.descrip = descrip;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return descrip;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public int getViewType() {
        return About.VIEW_ABOUT;
    }
}
