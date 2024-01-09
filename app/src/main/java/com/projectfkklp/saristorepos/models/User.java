package com.projectfkklp.saristorepos.models;

import com.projectfkklp.saristorepos.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String phoneNumber;
    private String phoneUid;
    private String gmail;
    private String gmailUid;


    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }
    public void setGmail(String gmail){
        this.gmail=gmail;
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
    public String getName() {
        return name;
    }
    public String getPhoneNumber(){
        return  phoneNumber;
    }
    public String getPhoneUid() {
        return phoneUid;
    }
    public String getGmail() {
        return gmail;
    }

    public String getGmailUid() {
        return gmailUid;
    }

    // TODO: details below need to be moved to Store model
    private List<Double> dailySales;
    private Date dailySalesUpdatedAt;

    public User() {
        id = ModelUtils.createUUID();
        this.dailySales = new ArrayList<>();
        this.dailySalesUpdatedAt = new Date();
    }
    public User(String name, String phoneUid, String gmailUid){
        this.name = name;
        this.phoneUid = phoneUid;
        this.gmailUid = gmailUid;
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
