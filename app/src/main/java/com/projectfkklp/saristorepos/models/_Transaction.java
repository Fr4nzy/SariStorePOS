package com.projectfkklp.saristorepos.models;

import androidx.annotation.NonNull;

import com.projectfkklp.saristorepos.utils.DateUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class _Transaction {
    private String date;
    private double amount;
    private String id;

    private List<Product> items;

    // Empty constructor needed for Firestore
    public _Transaction() {
        items = new ArrayList<>();
    }

    public _Transaction(String uid, Date date, double amount, List<Product> items) {
        this.id = uid;
        this.date = DateUtils.formatTimestamp(date);
        this.amount = amount;
        this.items = items;
    }

    public _Transaction(String uuid, String timestampString, double amount) {
        this.id = uuid;
        this.date = timestampString;
        this.amount = amount;
        this.items = new ArrayList<>();
    }

    public List<Product> getItems() {
        return items;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public int getTotalQuantity(){
        int totalQuantity=0;

        for (Product item: items){
            totalQuantity+=item.getQuantity();
        }

        return totalQuantity;
    }

    @NonNull
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String itemsSummary = "";

        for (Product item: items){
            itemsSummary+=
                    "\n"+item.getQuantity()+"x "+item.getProduct()
                    +"\n\t\tUnit Price: ₱"+ decimalFormat.format(item.getPrice())
                    +"/ Amount: ₱"+ decimalFormat.format(item.getTotalPrice())
            ;
        }

        return date + "\n"
                +itemsSummary
                +"\n"
                +"\nQuantity: "+ decimalFormat.format(getTotalQuantity())
                +"\nTotal Price: ₱"+decimalFormat.format(amount)
        ;
    }
}