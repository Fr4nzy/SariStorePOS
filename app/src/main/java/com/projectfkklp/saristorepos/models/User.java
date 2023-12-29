package com.projectfkklp.saristorepos.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private List<Double> dailySales;
    private Date dailySalesUpdatedAt;

    public User() {
        this.dailySales = new ArrayList<>();
        this.dailySalesUpdatedAt = new Date();
    }
    public User(List<Double> dailySales, Date dailySalesUpdatedAt) {
        this.dailySales = dailySales;
        this.dailySalesUpdatedAt = dailySalesUpdatedAt;
    }

    // Getter for dailySales
    public List<Double> getDailySales() {
        return dailySales;
    }

    // Setter for dailySales
    public void setDailySales(List<Double> dailySales) {
        this.dailySales = dailySales;
    }

    // Getter for dailySalesUpdatedAt
    public Date getDailySalesUpdatedAt() {
        return dailySalesUpdatedAt;
    }

    // Setter for dailySalesUpdatedAt
    public void setDailySalesUpdatedAt(Date dailySalesUpdatedAt) {
        this.dailySalesUpdatedAt = dailySalesUpdatedAt;
    }
}
