package com.projectfkklp.saristorepos.models;

import androidx.annotation.NonNull;

import com.projectfkklp.saristorepos.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Store {
    private String id;
    private String name;
    private String address;
    private ArrayList<Product> products;
    private List<Integer> dailySold;
    private List<Double> dailySales;
    private Date dailySalesUpdatedAt;

    public Store(){
        id = ModelUtils.createShortId();
        products = new ArrayList<>();
        dailySold = new ArrayList<>();
        dailySales = new ArrayList<>();
        dailySalesUpdatedAt = new Date();
    }

    public Store(String id, String name){
        this.id = id;
        this.name = name;
        products = new ArrayList<>();
    }

    public Store(String id, String name, String address){
        this.id = id;
        this.name = name;
        this.address = address;
        products = new ArrayList<>();
    }

    // Getter and Setter methods for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter methods for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter methods for 'name'
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Product> getProducts(){
        return products;
    }

    // Getter for dailySold
    public List<Integer> getDailySold() {
        return dailySold;
    }

    // Setter for dailySold
    public void setDailySold(List<Integer> dailySold) {
        this.dailySold = dailySold;
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

    @NonNull
    public Store clone(){
        return new Store(
            getId(),
            getName(),
            getAddress()
        );
    }
}
