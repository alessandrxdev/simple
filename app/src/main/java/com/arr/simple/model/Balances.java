package com.arr.simple.model;

public class Balances {

    private String title, balance, vencimiento;
    private int icon;

    public Balances(String title, String balance, String vencimiento, int icon) {
        this.title = title;
        this.balance = balance;
        this.vencimiento = vencimiento;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getBalance() {
        return balance;
    }

    public String getVence() {
        return vencimiento;
    }

    public int getIcon() {
        return icon;
    }
}
