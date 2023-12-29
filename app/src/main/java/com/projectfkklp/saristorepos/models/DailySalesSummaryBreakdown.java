package com.projectfkklp.saristorepos.models;

public class DailySalesSummaryBreakdown {
    private String product;
    private int quantity;
    private double unitPrice;

    public DailySalesSummaryBreakdown() {
        this.product = "";
        this.quantity = 0;
        this.unitPrice = 0.0;
    }

    //Setters method
    public void setProduct(String product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Getter methods

    public double getTotalPrice() {
        return quantity * unitPrice;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
