package com.projectfkklp.saristorepos.models;

import androidx.annotation.NonNull;

import com.projectfkklp.saristorepos.utils.ModelUtils;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Store {
    private String id;
    private String name;
    private String address;
    private ArrayList<Product> products;

    public Store(){
        id = ModelUtils.createShortId();
        products = new ArrayList<>();
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

    @NonNull
    public Store clone(){
        return new Store(
            getId(),
            getName(),
            getAddress()
        );
    }
}
