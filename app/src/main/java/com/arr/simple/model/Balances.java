package com.arr.simple.model;

public class Balances {

    private String title, vencimiento;
    private int icon;

    public Balances(String title, String vencimiento, int icon) {
        this.title = title;
        this.vencimiento = vencimiento;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getVence() {
        return vencimiento;
    }

    public int getIcon() {
        return icon;
    }
}
