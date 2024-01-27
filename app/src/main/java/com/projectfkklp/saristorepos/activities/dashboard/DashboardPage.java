package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.analytics.AnalyticsPage;
import com.projectfkklp.saristorepos.activities.inventory.InventoryProductListPage;
import com.projectfkklp.saristorepos.activities.pos.PosPage;
import com.projectfkklp.saristorepos.activities.store_profile.StoreProfilePage;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.transaction.TransactionPage;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfilePage;
import com.projectfkklp.saristorepos.utils.CacheUtils;
import com.projectfkklp.saristorepos.managers.StoreManager;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;

public class DashboardPage extends AppCompatActivity {
    SwipeRefreshLayout swipeRefresh;
    DashboardSalesForecastChart analyticsChart;
    DashboardTopChart topSellingChart;
    DashboardTopChart topSoldChart;
    DashboardTodaySalesChart todaySalesChart;

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
        swipeRefresh = findViewById(R.id.dashboard_swipe_refresh);
        analyticsChart = findViewById(R.id.dashboard_analytics_chart);

        todaySalesChart = findViewById(R.id.dashboard_today_sales_chart);
        topSellingChart = findViewById(R.id.dashboard_top_selling_chart);
        topSoldChart = findViewById(R.id.dashboard_top_sold_chart);

        swipeRefresh.setOnRefreshListener(()->{
            swipeRefresh.setRefreshing(false);
        });

        todaySalesChart.initializeTodaySalesChart("Today Sales");
        topSellingChart.initializePieChart("Top Selling Product");
        topSoldChart.initializePieChart("Top Sold Products");
    }

    private void generateAnalyticsChart(){
        float[] actualSales = generateRandomDoubleArray(7);
        float[] forecastSales = generateRandomDoubleArray(10);
        analyticsChart.setData(actualSales, forecastSales);
    }

    private void generateTodaySalesChart() {
        float ySales = 4_545_454_545.454545F;
        float taSales = 5_000_000_000F;
        float ttSales = 100_000_000_00f;

        todaySalesChart.setData(ySales, taSales, ttSales);
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
                String formattedValue = StringUtils.formatToPesoWithMetricPrefix(value);

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

    public void gotoPos(View view){
        startActivity(new Intent(this, PosPage.class));
    }

    public void gotoTransaction(View view){
        startActivity(new Intent(this, TransactionPage.class));
    }

    public void gotoInventory(View view){
        startActivity(new Intent(this, InventoryProductListPage.class));
    }

    public void gotoAnalytics(View view){
        startActivity(new Intent(this, AnalyticsPage.class));
    }

    public void gotoStoreProfile(View view){
        startActivity(new Intent(this, StoreProfilePage.class));
    }

    public void gotoProfile(View view){
        startActivity(new Intent(this, UserProfilePage.class));
    }
}