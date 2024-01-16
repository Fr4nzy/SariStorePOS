package com.projectfkklp.saristorepos.activities.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        // Inflate the layout using DataBinding
        // To make it possible to pass data in reusable layout
        DataBindingUtil.setContentView(this,R.layout.dashboard_page);

        initializeViews();

        // Dashboard Cards
        generateAnalyticsChart();
        generateTodaySalesChart();
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

    private int getRandomSalesValue() {
        // Adjust the range of random sales values as needed
        return (int) (Math.random() * 1000) + 100; // Example range: 100 to 1100
    }

    private void generateTodaySalesChart() {
        HashMap<String, Integer> todaySalesEntries = new HashMap<>();
        todaySalesEntries.put("Product 1", getRandomSalesValue());
        todaySalesEntries.put("Product 2", getRandomSalesValue());
        todaySalesEntries.put("Product 3", getRandomSalesValue());
        todaySalesEntries.put("Product 4", getRandomSalesValue());
        todaySalesEntries.put("Product 5", getRandomSalesValue());

        int totalTodaySales = todaySalesEntries.values().stream().mapToInt(Integer::intValue).sum();
        todaySalesChart.setData(todaySalesEntries, new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (totalTodaySales == 0) {
                    return "N/A"; // Avoid division by zero
                }

                float percentValue = (value / totalTodaySales) * 100;
                String formattedValue = StringUtils.formatToPeso(value);

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedPercent = decimalFormat.format(percentValue);

                return String.format("%s (%s%%)", formattedValue, formattedPercent);
            }
        });
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