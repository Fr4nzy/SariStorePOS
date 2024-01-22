package com.projectfkklp.saristorepos.models;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class _DailySalesSummary {
    private ArrayList<_DailySalesSummaryBreakdown> breakdownList = new ArrayList<>();
    private Date date;

    public _DailySalesSummary(Date date, ArrayList<_DailySalesSummaryBreakdown> breakdownList) {
        this.date = date;
        this.breakdownList = breakdownList;
    }

    //Setters method
    public void setDate(Date date) {
        this.date = date;
    }

    public void setSummaryList(ArrayList<_DailySalesSummaryBreakdown> breakdownList) {
        this.breakdownList = breakdownList;
    }

    //Getters method
    public Date getDate() {
        return this.date;
    }

    public void getBreakdownList(ArrayList<_DailySalesSummaryBreakdown> breakdownList) {
        this.breakdownList = breakdownList;
    }

    public int getTotalOverallItems() {
        int totalItems = 0;

        for (_DailySalesSummaryBreakdown breakdown : breakdownList) {
            totalItems += breakdown.getQuantity(); // Assuming there is a getQuantity() method in _DailySalesSummaryBreakdown
        }

        return totalItems;
    }

    public int getTotalQuantity() {
        int totalQuantity = 0;

        for (_DailySalesSummaryBreakdown breakdown : breakdownList) {
            totalQuantity += breakdown.getQuantity();
        }

        return totalQuantity;
    }

    public double getTotalPrice() {
        double totalPrice = 0.0;

        for (_DailySalesSummaryBreakdown breakdown : breakdownList) {
            totalPrice += breakdown.getTotalPrice();
        }

        return totalPrice;
    }


    public String getContent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String summaryContent = dateFormat.format(date) + "\n\n";
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        for (_DailySalesSummaryBreakdown breakdown : breakdownList) {
            summaryContent +=
                    breakdown.getProduct() + "\n"
                            + "Quantity: " + breakdown.getQuantity() + "\n"
                            + "Unit Price: ₱" + decimalFormat.format(breakdown.getUnitPrice()) + "\n"
                            + "Total Price: ₱" + decimalFormat.format(breakdown.getTotalPrice()) + "\n"
                            + "\n"
            ;
        }

        summaryContent +=
                "Total Sold items: ₱" + decimalFormat.format(getTotalQuantity())
                        + "\n\n"
                        + "Total Sales: ₱" + decimalFormat.format(getTotalPrice())
                        + "\n\n";

        return summaryContent;
    }

}
