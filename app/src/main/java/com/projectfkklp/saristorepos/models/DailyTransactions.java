package com.projectfkklp.saristorepos.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyTransactions implements Serializable {
    private String date;
    private List<Transaction> transactions;

    public DailyTransactions() {

    }

    public DailyTransactions(String date) {
        this.date = date;
        this.transactions = new ArrayList<>();
    }

    public DailyTransactions(String date, List<Transaction> transactions) {
        this.date = date;
        this.transactions = transactions;
    }

    // Getter method for 'date'
    public String getDate() {
        return date;
    }

    // Setter method for 'date'
    public void setDate(String date) {
        this.date = date;
    }

    // Getter method for 'transactions'
    public List<Transaction> getTransactions() {
        return transactions;
    }

    // Setter method for 'transactions'
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Exclude
    public int calculateTotalSoldItems(){
        if (transactions == null) {
            return 0;
        }

        int totalSoldItems = 0;

        for (Transaction transaction : transactions) {
            totalSoldItems += transaction.calculateTotalQuantity();
        }

        return totalSoldItems;
    }

    @Exclude
    public float calculateTotalSales(){
        if (transactions == null) {
            return 0;
        }

        float totalSales = 0;

        for (Transaction transaction : transactions) {
            totalSales += transaction.calculateTotalSales();
        }

        return totalSales;
    }
}
