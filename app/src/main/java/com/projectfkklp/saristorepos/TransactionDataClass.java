package com.projectfkklp.saristorepos;

public class TransactionDataClass {
    private String date;
    private int totalAmount;
    private String transactionId;

    // Empty constructor needed for Firestore
    public TransactionDataClass() {
    }

    public TransactionDataClass(String date, int totalAmount, String transactionId) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
