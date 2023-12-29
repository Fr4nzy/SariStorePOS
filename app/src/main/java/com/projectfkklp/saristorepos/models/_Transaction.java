package com.projectfkklp.saristorepos.models;

import com.google.type.DateTime;

import java.util.List;

public class _Transaction {
    // region Fields

    private String id;
    private DateTime datetime;
    private int totalAmount;
    private List<_TransactionItem> items;

    // endregion

    // region Getters

    public String getId() {
        return id;
    }

    public DateTime getDatetime() {
        return datetime;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public List<_TransactionItem> getItems() {
        return items;
    }

    // endregion

    // region Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setDatetime(DateTime datetime) {
        this.datetime = datetime;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setItems(List<_TransactionItem> items) {
        this.items = items;
    }

    // endregion
}
