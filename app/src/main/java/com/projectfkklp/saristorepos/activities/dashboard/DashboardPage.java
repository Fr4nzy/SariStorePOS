package com.projectfkklp.saristorepos.activities.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.loadinganimation.LoadingAnimation;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.analytics.AnalyticsPage;
import com.projectfkklp.saristorepos.activities.inventory.InventoryProductListPage;
import com.projectfkklp.saristorepos.activities.pos.PosPage;
import com.projectfkklp.saristorepos.activities.store_profile.StoreProfilePage;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.activities.transaction.TransactionPage;
import com.projectfkklp.saristorepos.activities.user_profile.UserProfilePage;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.repositories.ReportRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.utils.CacheUtils;
import com.projectfkklp.saristorepos.utils.NumberUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardPage extends AppCompatActivity {
    TextView storeNameText;
    TextView storeAddressText;
    SwipeRefreshLayout swipeRefresh;
    DashboardSalesForecastChart analyticsChart;
    DashboardTopChart topSellingChart;
    DashboardTopChart topSoldChart;
    DashboardTodaySalesChart todaySalesChart;
    LoadingAnimation forecastLoading;

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);

        // Inflate the layout using DataBinding
        // To make it possible to pass data in reusable layout
        DataBindingUtil.setContentView(this,R.layout.dashboard_page);

        store = SessionRepository.getCurrentStore(this);

        initializeViews();

        // ignore the code below, it's a fix to the lay outing issue
        analyticsChart.setData(new float[]{}, new float[]{});

        generateCharts();
    }

    private void generateCharts(){
        generateAnalyticsChart();
        generateTodaySalesChart();
        generateTopSellingChart();
        generateTopSoldChart();
    }

    private void initializeViews(){
        storeNameText = findViewById(R.id.dashboard_store_name);
        storeAddressText = findViewById(R.id.dashboard_store_address);

        swipeRefresh = findViewById(R.id.dashboard_swipe_refresh);
        analyticsChart = findViewById(R.id.dashboard_analytics_chart);

        todaySalesChart = findViewById(R.id.dashboard_today_sales_chart);
        topSellingChart = findViewById(R.id.dashboard_top_selling_chart);
        topSoldChart = findViewById(R.id.dashboard_top_sold_chart);

        forecastLoading = findViewById(R.id.dashboard_forecast_loading);

        swipeRefresh.setOnRefreshListener(()->{
            generateCharts();
            swipeRefresh.setRefreshing(false);
        });

        storeNameText.setText(store.getName());
        storeAddressText.setText(store.getAddress());
        todaySalesChart.initializeTodaySalesChart("Today Sales");
        topSellingChart.initializePieChart("Top Selling Product");
        topSoldChart.initializePieChart("Top Sold Products");
    }

    private void generateAnalyticsChart(){
        analyticsChart.setVisibility(View.INVISIBLE);
        forecastLoading.setVisibility(View.VISIBLE);
        ReportRepository.getRecentSalesAndForecastWithHistoryReport(this)
            .addOnSuccessListener(task->{
                Pair<List<Double>, List<Double>> recentSalesAndForecastWithHistoryReport = task.getResult();
                List<Double> recentSales = recentSalesAndForecastWithHistoryReport.first;
                List<Double> forecastWithHistory = recentSalesAndForecastWithHistoryReport.second;

                analyticsChart.setData(
                    NumberUtils.convertToFloatArray(recentSales),
                    NumberUtils.convertToFloatArray(forecastWithHistory)
                );
            })
            .addOnFailureListener(e-> ToastUtils.show(this, e.getMessage()))
            .addOnCompleteListener(task-> {
                analyticsChart.setVisibility(View.VISIBLE);
                forecastLoading.setVisibility(View.GONE);
            })
        ;
    }

    private void generateTodaySalesChart() {
        // Show loading and hide chart
        TestingUtils.delay(1500, ()->{
            float ySales = 4_545_454_545.454545F;
            float taSales = 5_000_000_000F;
            float ttSales = 100_000_000_00f;
            todaySalesChart.setData(ySales, taSales, ttSales);
            // Hide loading and show chart
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

    public void gotoStoreSelector(View view){
        startActivity(new Intent(this, StoreSelectorPage.class));
    }

    public void gotoPos(View view){
        CacheUtils.saveObjectList(this, "transaction_items", new ArrayList<>());
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