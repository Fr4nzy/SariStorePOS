package com.projectfkklp.saristorepos.activities.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.GridLayout;
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
import com.projectfkklp.saristorepos.classes.ProductSalesSummaryData;
import com.projectfkklp.saristorepos.classes.SalesAndSoldItemsReportData;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.DailyTransactionsRepository;
import com.projectfkklp.saristorepos.repositories.ReportRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.CacheUtils;
import com.projectfkklp.saristorepos.utils.NumberUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DashboardPage extends AppCompatActivity {
    TextView storeNameText;
    TextView storeAddressText;
    GridLayout salesAndSoldItemsGrid;
    TextView ssiWeekSalesText, ssiWeekSalesGrowthPercText,
            ssiWeekSoldItemsText, ssiWeekSoldItemsGrowthPercText,
            ssiMonthSalesText, ssiMonthSalesGrowthPercText,
            ssiMonthSoldItemsText, ssiMonthSoldItemsGrowthPercText,
            ssiYearSalesText, ssiYearSalesGrowthPercText,
            ssiYearSoldItemsText, ssiYearSoldItemsGrowthPercText;
    SwipeRefreshLayout swipeRefresh;
    DashboardSalesForecastChart analyticsChart;
    DashboardTopChart topSellingChart;
    DashboardTopChart topSoldChart;
    DashboardTodaySalesChart todaySalesChart;
    LoadingAnimation forecastLoading;
    LoadingAnimation todaySalesLoading;
    LoadingAnimation topSellingLoading;
    LoadingAnimation topSoldLoading;
    LoadingAnimation saleAndSoldItemsLoading;

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
        generateSalesAndSoldItemsReport();
        generateProductRelatedReports();
    }

    private void initializeViews(){
        storeNameText = findViewById(R.id.dashboard_store_name);
        storeAddressText = findViewById(R.id.dashboard_store_address);

        swipeRefresh = findViewById(R.id.dashboard_swipe_refresh);
        analyticsChart = findViewById(R.id.dashboard_analytics_chart);

        todaySalesChart = findViewById(R.id.dashboard_today_sales_chart);
        topSellingChart = findViewById(R.id.dashboard_top_selling_chart);
        topSoldChart = findViewById(R.id.dashboard_top_sold_chart);
        salesAndSoldItemsGrid = findViewById(R.id.dashboard_sales_and_sold_grid);

        // Sales and Sold Items Views
        ssiWeekSalesText = findViewById(R.id.dashboard_ssi_week_sales);
        ssiWeekSalesGrowthPercText = findViewById(R.id.dashboard_ssi_week_growth_perc);
        ssiWeekSoldItemsText = findViewById(R.id.dashboard_current_week_sold_report);
        ssiWeekSoldItemsGrowthPercText = findViewById(R.id.dashboard_previous_week_sold_report);
        ssiMonthSalesText = findViewById(R.id.dashboard_current_month_sales_report);
        ssiMonthSalesGrowthPercText = findViewById(R.id.dashboard_previous_month_sales_report);
        ssiMonthSoldItemsText = findViewById(R.id.dashboard_current_month_sold_report);
        ssiMonthSoldItemsGrowthPercText = findViewById(R.id.dashboard_previous_month_sold_report);
        ssiYearSalesText = findViewById(R.id.dashboard_current_year_sales_report);
        ssiYearSalesGrowthPercText = findViewById(R.id.dashboard_previous_year_sales_report);
        ssiYearSoldItemsText = findViewById(R.id.dashboard_current_year_sold_report);
        ssiYearSoldItemsGrowthPercText = findViewById(R.id.dashboard_previous_year_sold_report);

        // Loading Animations Views
        forecastLoading = findViewById(R.id.dashboard_forecast_loading);
        todaySalesLoading = findViewById(R.id.dashboard_today_sales_loading);
        topSellingLoading = findViewById(R.id.dashboard_top_selling_loading);
        topSoldLoading = findViewById(R.id.dashboard_top_sold_loading);
        saleAndSoldItemsLoading = findViewById(R.id.dashboard_sales_and_sold_items_loading);

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

                    if (recentSales.isEmpty()){
                        analyticsChart.setData(null);
                        return;
                    }

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
        todaySalesChart.setVisibility(View.INVISIBLE);
        todaySalesLoading.setVisibility(View.VISIBLE);
        ReportRepository.getTodaySalesReport(this)
                .addOnSuccessListener(task->{
                    List<Double> todaySalesData = task.getResult();
                    Double yesterdaySales = todaySalesData.get(0);
                    Double todayActualSales = todaySalesData.get(1);
                    Double todayTargetSales = todaySalesData.get(2);

                    if (yesterdaySales < 0 && todayActualSales < 0 && todayTargetSales < 0){
                        todaySalesChart.setData(null);
                        return;
                    }

                    // Set data on todaySalesChart (Replace with the actual method or property names)
                    todaySalesChart.setData(
                            NumberUtils.convertToFloat(yesterdaySales),
                            NumberUtils.convertToFloat(todayActualSales),
                            NumberUtils.convertToFloat(todayTargetSales));
                })
                .addOnFailureListener(e-> ToastUtils.show(this, e.getMessage()))
                .addOnCompleteListener(task-> {
                    todaySalesChart.setVisibility(View.VISIBLE);
                    todaySalesLoading.setVisibility(View.GONE);
                })
        ;
    }
    @SuppressLint("DefaultLocale")
    private void generateSalesAndSoldItemsReport() {
        salesAndSoldItemsGrid.setVisibility(View.INVISIBLE);
        saleAndSoldItemsLoading.setVisibility(View.VISIBLE);

        ReportRepository.getSalesAndSoldItemsReport(this)
            .addOnSuccessListener(task -> {
                SalesAndSoldItemsReportData reportData = task.getResult();

                // Week Data
                setSsiGridData(
                    reportData.weekCurrentSales,
                    reportData.weekPreviousSales,
                    ssiWeekSalesText,
                    ssiWeekSalesGrowthPercText,
                    reportData.weekCurrentSoldItems,
                    reportData.weekPreviousSoldItems,
                    ssiWeekSoldItemsText,
                    ssiWeekSoldItemsGrowthPercText
                );
                // Month Data
                setSsiGridData(
                    reportData.monthCurrentSales,
                    reportData.monthPreviousSales,
                    ssiMonthSalesText,
                    ssiMonthSalesGrowthPercText,
                    reportData.monthCurrentSoldItems,
                    reportData.monthPreviousSoldItems,
                    ssiMonthSoldItemsText,
                    ssiMonthSoldItemsGrowthPercText
                );
                // Year Data
                setSsiGridData(
                    reportData.yearCurrentSales,
                    reportData.yearPreviousSales,
                    ssiYearSalesText,
                    ssiYearSalesGrowthPercText,
                    reportData.yearCurrentSoldItems,
                    reportData.yearPreviousSoldItems,
                    ssiYearSoldItemsText,
                    ssiYearSoldItemsGrowthPercText
                );
            })
            .addOnFailureListener(e -> ToastUtils.show(this, e.getMessage()))
            .addOnCompleteListener(dailyTransactionsTask -> {
                // Hide loading, and show charts
                salesAndSoldItemsGrid.setVisibility(View.VISIBLE);
                saleAndSoldItemsLoading.setVisibility(View.GONE);
            })
        ;
    }

    @SuppressLint("DefaultLocale")
    private void setSsiGridData(
        float currentSales,
        float previousSales,
        TextView salesText,
        TextView salesGrowthPercText,
        int currentSoldItems,
        int previousSoldItems,
        TextView soldItemsText,
        TextView soldItemsGrowthPercText
    ) {
        salesText.setText(StringUtils.formatToPesoWithMetricPrefix(currentSales));
        if (previousSales == 0){
            salesGrowthPercText.setText("N/A");
        }
        else {
            float rate = (currentSales/previousSales) * 100;
            salesGrowthPercText.setText(String.format(
                "%+.2f%%",
                rate
            ));
            salesGrowthPercText.setTextColor(
                rate < 0
                    ? Color.parseColor("#4CAF50")
                    : Color.parseColor("#388E3C")
            );
        }

        soldItemsText.setText(StringUtils.formatWithMetricPrefix(currentSoldItems));
        if (previousSoldItems == 0){
            soldItemsGrowthPercText.setText("N/A");
        }
        else {
            float rate = ((float) currentSoldItems /previousSoldItems) * 100;
            soldItemsGrowthPercText.setText(String.format(
                "%+.2f%%",
                rate
            ));
            soldItemsGrowthPercText.setTextColor(
                rate < 0
                    ? Color.parseColor("#4CAF50")
                    : Color.parseColor("#388E3C")
            );
        }
    }

    private void generateProductRelatedReports(){
        topSellingLoading.setVisibility(View.VISIBLE);
        topSoldLoading.setVisibility(View.VISIBLE);
        topSellingChart.setVisibility(View.INVISIBLE);
        topSoldChart.setVisibility(View.INVISIBLE);

        // Fetch transactions from last 30 days up today
        StoreRepository.getStoreById(SessionRepository.getCurrentStore(this).getId())
            .addOnSuccessListener(task->{
                // Use store to reference products details, like product name
                store = task.toObject(Store.class);
                assert store != null;
                List<Product> products = store.getProducts();

                DailyTransactionsRepository.getDailyTransactions(this, 0)
                    .addOnSuccessListener(dailyTransactionsTask->{
                        List<DailyTransactions> dailyTransactionsList = dailyTransactionsTask.toObjects(DailyTransactions.class);

                        // Data Preparation
                        HashMap<String, ProductSalesSummaryData> hashedProductSalesSummaryData = new HashMap<>();
                        for (DailyTransactions dailyTransactions : dailyTransactionsList){
                            for (Transaction transaction: dailyTransactions.getTransactions()){
                                for (TransactionItem transactionItem:transaction.getItems()){
                                    String key = transactionItem.getProductId();

                                    // Existing entry/Appending
                                    if (hashedProductSalesSummaryData.containsKey(key)){
                                        ProductSalesSummaryData summary = hashedProductSalesSummaryData.get(key);
                                        assert summary != null;
                                        summary.soldItems += transactionItem.getQuantity();
                                        summary.sales += transactionItem.calculateAmount();
                                        continue;
                                    }

                                    // New entry
                                    Optional<Product> optionalProduct = products.stream().filter(p->p.getId().equals(key)).findFirst();
                                    if (!optionalProduct.isPresent()) {
                                        continue;
                                    }

                                    Product product = optionalProduct.get();
                                    ProductSalesSummaryData summaryData = new ProductSalesSummaryData(
                                            key,
                                            product.getName(),
                                            transactionItem.getQuantity(),
                                            transactionItem.calculateAmount()
                                    );
                                    hashedProductSalesSummaryData.put(key, summaryData);
                                }
                            }
                        }

                        // Check if the data is empty
                        if (hashedProductSalesSummaryData.isEmpty()) {
                            // Set chart data as null
                            topSellingChart.setData(null);
                            topSoldChart.setData(null);

                            // Hide loading and show charts
                            topSellingLoading.setVisibility(View.GONE);
                            topSoldLoading.setVisibility(View.GONE);
                            topSellingChart.setVisibility(View.VISIBLE);
                            topSoldChart.setVisibility(View.VISIBLE);

                            return;
                        }

                        // Prepare data for top-selling chart
                        HashMap<String, Integer> topSellingData = new HashMap<>();
                        hashedProductSalesSummaryData.forEach((key, summary) -> topSellingData.put(summary.productName, (int) summary.sales));

                        // Prepare data for top-sold chart
                        HashMap<String, Integer> topSoldData = new HashMap<>();
                        hashedProductSalesSummaryData.forEach((key, summary) -> topSoldData.put(summary.productName, summary.soldItems));

                        // Generate charts
                        generateTopSellingChart(topSellingData);
                        generateTopSoldChart(topSoldData);
                    })
                    .addOnFailureListener(e-> ToastUtils.show(this, e.getMessage()))
                    .addOnCompleteListener(dailyTransactionsTask-> {
                        topSellingLoading.setVisibility(View.GONE);
                        topSoldLoading.setVisibility(View.GONE);
                        topSellingChart.setVisibility(View.VISIBLE);
                        topSoldChart.setVisibility(View.VISIBLE);
                    })
                ;
            })
            .addOnFailureListener(e-> {
                ToastUtils.show(this, e.getMessage());
                topSellingChart.setVisibility(View.VISIBLE);
                topSoldChart.setVisibility(View.VISIBLE);
                topSellingLoading.setVisibility(View.GONE);
                topSoldLoading.setVisibility(View.GONE);
            })
        ;
    }

    private void generateTopSellingChart(HashMap<String, Integer> salesEntries) {
        int total = salesEntries.values().stream().mapToInt(Integer::intValue).sum();
        topSellingChart.setData(salesEntries, new ValueFormatter() {
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

    private void generateTopSoldChart(HashMap<String, Integer> soldEntries) {
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