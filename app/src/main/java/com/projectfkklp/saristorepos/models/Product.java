package com.projectfkklp.saristorepos.models;

import com.projectfkklp.saristorepos.enums.Status;

import java.io.Serializable;

public class Product implements Serializable {
    // region Fields
    private String id;
    private String name;
    private int stocks;
    private float unitPrice;
    private String imgUrl;
    private String barcode;
    private Status status;
    // endregion

    public Product(){
        status = Status.ACTIVE;
    }

    public Product(String id, String name, int stocks, float unitPrice, String imgUrl, String barcode){
        this.id = id;
        this.name = name;
        this.stocks = stocks;
        this.unitPrice = unitPrice;
        this.imgUrl=imgUrl;
        this.barcode=barcode;
        status = Status.ACTIVE;
    }

    // region Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStocks() {
        return stocks;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getBarcode() {
        return barcode;
    }
    public Status getStatus() {
        return status;
    }

    // endregion

    // region Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStocks(int stocks) {
        this.stocks = stocks;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // endregion
}
