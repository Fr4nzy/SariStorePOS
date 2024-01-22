package com.projectfkklp.saristorepos.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class DailyTransactions implements Serializable {
    private LocalDate date;
    private ArrayList<Transaction> transactions;

    public DailyTransactions() {

    }

    public DailyTransactions(LocalDate date, ArrayList<Transaction> transactions) {
        this.date = date;
        this.transactions = transactions;
    }

    // Getter method for 'date'
    public LocalDate getDate() {
        return date;
    }

    // Setter method for 'date'
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter method for 'transactions'
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    // Setter method for 'transactions'
    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getTotalSoldItems(){
        if (transactions == null) {
            return 0;
        }

        int totalSoldItems = 0;

        for (Transaction transaction : transactions) {
            totalSoldItems += transaction.getTotalQuantity();
        }

        return totalSoldItems;
    }

    public float getTotalSales(){
        if (transactions == null) {
            return 0;
        }

        float totalSales = 0;

        for (Transaction transaction : transactions) {
            totalSales += transaction.getTotalSales();
        }

        return totalSales;
    }
}
