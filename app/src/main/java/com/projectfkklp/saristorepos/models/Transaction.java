package com.projectfkklp.saristorepos.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Serializable {
    private LocalDateTime dateTime;
    private List<TransactionItem> items;

    public Transaction() {

    }

    public Transaction(LocalDateTime time, List<TransactionItem> items) {
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
    public List<TransactionItem> getItems() {
        return items;
    }

    // Setter for 'items'
    public void setItems(List<TransactionItem> items) {
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
