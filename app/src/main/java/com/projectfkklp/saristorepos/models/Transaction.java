package com.projectfkklp.saristorepos.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class Transaction implements Serializable {
    private String dateTime;
    private List<TransactionItem> items;

    public Transaction() {

    }

    public Transaction(String time, List<TransactionItem> items) {
        this.dateTime = time;
        this.items = items;
    }

    // Getter for 'time'
    public String getDateTime() {
        return dateTime;
    }

    // Setter for 'time'
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Getter for 'items'
    public List<TransactionItem> getItems() {
        return items;
    }

    // Setter for 'items'
    public void setItems(List<TransactionItem> items) {
        this.items = items;
    }

    @Exclude
    public int calculateTotalQuantity(){
        if (items == null) {
            return 0;
        }

        int totalQuantity = 0;

        for (TransactionItem item: items){
            totalQuantity+=item.getQuantity();
        }

        return totalQuantity;
    }

    @Exclude
    public float calculateTotalSales(){
        if (items == null) {
            return 0;
        }

        int totalQuantity = 0;

        for (TransactionItem item: items){
            totalQuantity+=item.calculateAmount();
        }

        return totalQuantity;
    }
}
