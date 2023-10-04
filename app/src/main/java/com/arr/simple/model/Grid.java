package com.arr.simple.model;

public class Grid implements Items {

    private int mIcon;
    private String mTitle, mSubtitle;

    public Grid(String title, String subtitle, int icon) {
        this.mTitle = title;
        this.mSubtitle = subtitle;
        this.mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getIcon() {
        return mIcon;
    }
    

    @Override
    public int getViewType() {
        return Grid.VIEW_GRID;
    }
}
