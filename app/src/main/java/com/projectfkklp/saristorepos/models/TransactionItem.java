package com.projectfkklp.saristorepos.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TransactionItem implements Serializable {
    private String productId;
    private int quantity;
    private float unitPrice; //on purchase, since product price can change overtime

    public TransactionItem() {

    }

    public TransactionItem(String productId, int quantity, float unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getter for 'productId'
    public String getProductId() {
        return productId;
    }

    // Setter for 'productId'
    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Getter for 'quantity'
    public int getQuantity() {
        return quantity;
    }

    // Setter for 'quantity'
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter for 'unitPrice'
    public float getUnitPrice() {
        return unitPrice;
    }

    // Setter for 'unitPrice'
    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getAmount() {
        return quantity * unitPrice;
    }

    @NonNull
    public TransactionItem clone(){
        return new TransactionItem(getProductId(), getQuantity(), getUnitPrice());
    }
}
