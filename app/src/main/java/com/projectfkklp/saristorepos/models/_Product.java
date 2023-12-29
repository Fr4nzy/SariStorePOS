package com.projectfkklp.saristorepos.models;

public class _Product {
    // region Fields

    private String id;
    private String name;
    private int stocks;
    private double unitPrice;
    private String imgUrl;
    private String barcode;

    // endregion

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

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getBarcode() {
        return barcode;
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

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    // endregion
}
