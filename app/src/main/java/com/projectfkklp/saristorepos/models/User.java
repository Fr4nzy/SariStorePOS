package com.projectfkklp.saristorepos.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String username;
    private String phoneUid;
    private String gmailUid;


    // Setter methods
    public void setId(String id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneUid(String phoneUid) {
        this.phoneUid = phoneUid;
    }

    public void setGmailUid(String gmailUid) {
        this.gmailUid = gmailUid;
    }

    // Getter methods
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public String getPhoneUid() {
        return phoneUid;
    }

    public String getGmailUid() {
        return gmailUid;
    }

    // TODO: details below need to be moved to Store model
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
