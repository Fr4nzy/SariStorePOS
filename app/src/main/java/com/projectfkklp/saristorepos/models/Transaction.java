package com.projectfkklp.saristorepos.models;

import java.time.LocalTime;
import java.util.ArrayList;

public class Transaction {
    private LocalTime time;
    private ArrayList<TransactionItem> items;

    public Transaction() {

    }

    public Transaction(LocalTime time, ArrayList<TransactionItem> items) {
        this.time = time;
        this.items = items;
    }

    // Getter for 'time'
    public LocalTime getTime() {
        return time;
    }

    // Setter for 'time'
    public void setTime(LocalTime time) {
        this.time = time;
    }

    // Getter for 'items'
    public ArrayList<TransactionItem> getItems() {
        return items;
    }

    // Setter for 'items'
    public void setItems(ArrayList<TransactionItem> items) {
        this.items = items;
    }

    public int getTotalQuantity(){
        if (items == null) {
            return 0;
        }

        int totalQuantity = 0;

        for (TransactionItem item: items){
            totalQuantity+=item.getQuantity();
        }

        return totalQuantity;
    }

    public float getTotalSales(){
        if (items == null) {
            return 0;
        }

        int totalQuantity = 0;

        for (TransactionItem item: items){
            totalQuantity+=item.getAmount();
        }

        return totalQuantity;
    }
}
