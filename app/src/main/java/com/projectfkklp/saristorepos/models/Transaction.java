package com.projectfkklp.saristorepos.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Transaction {
    private LocalDateTime dateTime;
    private ArrayList<TransactionItem> items;

    public Transaction() {

    }

    public Transaction(LocalDateTime time, ArrayList<TransactionItem> items) {
        this.dateTime = time;
        this.items = items;
    }

    // Getter for 'time'
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // Setter for 'time'
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
