package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_profile.StoreProfilePage;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfilePage;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;

public class DashboardPage extends AppCompatActivity {
    DashboardSalesLineChart analyticsChart;
    DashboardTopPieChart topSellingChart;
    DashboardTopPieChart topSoldChart;
    DashboardTopPieChart todaySalesChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);

        // Example values, Today Sales Data
        int overallSalesTarget = 5000;
        int actualSales = 3200;
        int incomeFromYesterday = 10;

        // Inflate the layout using DataBinding
        // To make it possible to pass data in reusable layout
        DataBindingUtil.setContentView(this,R.layout.dashboard_page);

        initializeViews();

        // Dashboard Cards
        generateAnalyticsChart();
        generateTodaySalesChart(overallSalesTarget, actualSales, incomeFromYesterday);
        generateTopSellingChart();
        generateTopSoldChart();
    }

    private void initializeViews(){
        analyticsChart = findViewById(R.id.dashboard_analytics_chart);

        todaySalesChart = findViewById(R.id.dashboard_today_sales_chart);
        topSellingChart = findViewById(R.id.dashboard_top_selling_chart);
        topSoldChart = findViewById(R.id.dashboard_top_sold_chart);

        todaySalesChart.initializePieChart("Today Sales");
        topSellingChart.initializePieChart("Top Selling Product");
        topSoldChart.initializePieChart("Top Sold Products");
    }

    private void generateAnalyticsChart(){
        float[] actualSales = generateRandomDoubleArray(7);
        float[] forecastSales = generateRandomDoubleArray(10);
        analyticsChart.setData(actualSales, forecastSales);
    }

    private void generateTodaySalesChart(int overallSalesTarget, int actualSales, int incomeFromYesterday) {
        // Calculate overall sales percentage
        float overallSalesPercentage = (float) actualSales / overallSalesTarget * 100;

        // Create a HashMap with the data to be displayed in the PieChart
        HashMap<String, Double> todaySalesEntries = new HashMap<>();
        todaySalesEntries.put("Actual Sales", (double) actualSales);
        todaySalesEntries.put("Target Sales", (double) overallSalesTarget - actualSales);
        todaySalesEntries.put("Income from Yesterday", (double) incomeFromYesterday);

        // Use the new initializeSalesPieChart method
        todaySalesChart.initializeSalesPieChart("Today Sales");

        // Set the data for the PieChart
        todaySalesChart.salesSetData(todaySalesEntries, new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        // Generate center text with customizable parameters
        String centerText = generateCenterText(overallSalesPercentage, actualSales, overallSalesTarget, incomeFromYesterday);

        // Set center text for the PieChart
        todaySalesChart.setCenterText(getSpannableString(centerText));
        todaySalesChart.setCenterTextSize(8f);

        // Move the center text to the center of the hole
        todaySalesChart.setCenterTextOffset(0f, 0f);
    }

    // Method to generate customizable center text
    private String generateCenterText(float overallSalesPercentage, int actualSales, int overallSalesTarget, int incomeFromYesterday) {
        // Change the color of specific text to green
        String centerText = String.format("Today Sales\n%.0f%% to Goal\nActual Sales: %s\nTarget Sales: %s\n\uD83D\uDCC8%d%% vs Yesterday",
                overallSalesPercentage,
                StringUtils.formatToPeso((float) actualSales),
                StringUtils.formatToPeso((float) (overallSalesTarget - actualSales)),
                incomeFromYesterday);

        // Modify the color of "Today Sales" and "10%"
        centerText = centerText.replace("Today Sales", "Today Sales ")
                .replace("10%", "10%").replace("64%", "64%");
        return centerText;
    }

    // Method to apply SpannableString for text styling
    private SpannableString getSpannableString(String text) {
        SpannableString spannableString = new SpannableString(text);

        // Change the color of "Today Sales" and "10%" to green
        int blueColor = Color.parseColor("#0000FF");
        int greenColor = Color.parseColor("#40C94F");
        int crimsonColor = Color.parseColor("#DC143C"); // This is the hex color code for Crimson


        int startIndex = text.indexOf("Today Sales");
        int endIndex = startIndex + 11; // Length of "Today Sales"
        spannableString.setSpan(new ForegroundColorSpan(blueColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("10%");
        endIndex = startIndex + 3; // Length of "10%"
        spannableString.setSpan(new ForegroundColorSpan(greenColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("64%");
        endIndex = startIndex + 3; // Length of "64%"
        spannableString.setSpan(new ForegroundColorSpan(crimsonColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }


    private void generateTopSellingChart() {
        HashMap<String, Integer> soldEntries = new HashMap<>();
        soldEntries.put("Sky flakes", 3000);
        soldEntries.put("Coke", 2000);
        soldEntries.put("Pepsi", 1000);
        soldEntries.put("Other 1", 900);
        soldEntries.put("Other 2", 800);
        soldEntries.put("Other 3", 300);

        int total = soldEntries.values().stream().mapToInt(Integer::intValue).sum();
        topSellingChart.setData(soldEntries, new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (total == 0) {
                    return "N/A"; // Avoid division by zero
                }

                float percentValue = (value / total) * 100;
                String formattedValue = StringUtils.formatToPeso(value);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedPercent = decimalFormat.format(percentValue);

                return String.format("%s (%s%%)", formattedValue, formattedPercent);
            }
        });
    }

    private void generateTopSoldChart() {
        HashMap<String, Integer> soldEntries = new HashMap<>();
        soldEntries.put("Sky flakes", 3000);
        soldEntries.put("Coke", 2000);
        soldEntries.put("Pepsi", 1000);
        soldEntries.put("Other 1", 900);
        soldEntries.put("Other 2", 800);
        soldEntries.put("Other 3", 300);

        int total = soldEntries.values().stream().mapToInt(Integer::intValue).sum();
        topSoldChart.setData(soldEntries, new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (total == 0) {
                    return "N/A"; // Avoid division by zero
                }

                float percentValue = (value / total) * 100;
                String formattedValue = StringUtils.formatWithMetricPrefix(value);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedPercent = decimalFormat.format(percentValue);

                return String.format("%s (%s%%)", formattedValue, formattedPercent);
            }
        });
    }

    private float[] generateRandomDoubleArray(int count){
        float[] values = new float[count];

        for (int i=0;i<count;i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }

    public void gotoStoreSelector(View view){
        startActivity(new Intent(this, StoreSelectorPage.class));
    }

    public void gotoStoreProfile(View view){
        startActivity(new Intent(this, StoreProfilePage.class));
    }

    public void gotoProfile(View view){
        startActivity(new Intent(this, UserProfilePage.class));
    }
}