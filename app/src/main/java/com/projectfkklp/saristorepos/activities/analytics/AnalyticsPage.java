package com.projectfkklp.saristorepos.activities.analytics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.loadinganimation.LoadingAnimation;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
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
import com.projectfkklp.saristorepos.utils.NumberUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class AnalyticsPage extends AppCompatActivity {
    TextView ssiWeekSalesText, ssiWeekSalesGrowthPercText,
            ssiWeekSoldItemsText, ssiWeekSoldItemsGrowthPercText,
            ssiMonthSalesText, ssiMonthSalesGrowthPercText,
            ssiMonthSoldItemsText, ssiMonthSoldItemsGrowthPercText,
            ssiYearSalesText, ssiYearSalesGrowthPercText,
            ssiYearSoldItemsText, ssiYearSoldItemsGrowthPercText;
    private SwipeRefreshLayout swipeRefresh;
    private AnalyticsSalesForecastChart analyticsChart;
    private AnalyticsTodaySalesChart todaySalesChart;
    private GridLayout salesAndSoldReport;
    private AnalyticsTopChart topSellingChart;
    private AnalyticsTopChart topSoldChart;
    private SortableTableView<ProductSalesSummaryData> productsSalesTable;

    LoadingAnimation forecastLoading;
    LoadingAnimation todaySalesLoading;
    LoadingAnimation salesAndSoldLoading;
    LoadingAnimation topSellingLoading;
    LoadingAnimation topSoldLoading;
    LoadingAnimation productsSalesTableLoading;

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics_page);

        initializeViews();

        // ignore the code below, it's a fix to the lay outing issue
        analyticsChart.setData(new float[]{}, new float[]{});

        generateCharts();

    }

    private void generateCharts(){
        generateAnalyticsChart();
        generateTodaySalesChart();
        generateProductRelatedReports();
        generateSalesAndSoldItems();
        generateProductsSalesReport();
    }

    private void initializeViews() {
        swipeRefresh = findViewById(R.id.analytics_swipe_refresh);
        analyticsChart = findViewById(R.id.analytics_forecast_chart);

        todaySalesChart = findViewById(R.id.analytics_today_sales_chart);
        salesAndSoldReport = findViewById(R.id.analytics_sales_and_sold_report);
        topSellingChart = findViewById(R.id.analytics_top_selling_chart);
        topSoldChart = findViewById(R.id.analytics_top_sold_chart);
        productsSalesTable = findViewById(R.id.analytics_products_sales_table);

        productsSalesTable = findViewById(R.id.analytics_products_sales_table);

        // Sales and Sold Items Views
        ssiWeekSalesText = findViewById(R.id.analytics_ssi_week_sales);
        ssiWeekSalesGrowthPercText = findViewById(R.id.analytics_ssi_week_growth_perc);
        ssiWeekSoldItemsText = findViewById(R.id.analytics_current_week_sold_report);
        ssiWeekSoldItemsGrowthPercText = findViewById(R.id.analytics_previous_week_sold_report);
        ssiMonthSalesText = findViewById(R.id.analytics_current_month_sales_report);
        ssiMonthSalesGrowthPercText = findViewById(R.id.analytics_previous_month_sales_report);
        ssiMonthSoldItemsText = findViewById(R.id.analytics_current_month_sold_report);
        ssiMonthSoldItemsGrowthPercText = findViewById(R.id.analytics_previous_month_sold_report);
        ssiYearSalesText = findViewById(R.id.analytics_current_year_sales_report);
        ssiYearSalesGrowthPercText = findViewById(R.id.analytics_previous_year_sales_report);
        ssiYearSoldItemsText = findViewById(R.id.analytics_current_year_sold_report);
        ssiYearSoldItemsGrowthPercText = findViewById(R.id.analytics_previous_year_sold_report);

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            generateCharts();
        });

        todaySalesChart.initializeTodaySalesChart("Today Sales");
        topSellingChart.initializePieChart("Top Selling Product");
        topSoldChart.initializePieChart("Top Sold Products");

        // Loadings
        forecastLoading = findViewById(R.id.analytics_forecast_loading);
        todaySalesLoading = findViewById(R.id.analytics_today_sales_loading);
        salesAndSoldLoading = findViewById(R.id.analytics_sales_and_sold_loading);
        topSellingLoading = findViewById(R.id.analytics_top_selling_loading);
        topSoldLoading = findViewById(R.id.analytics_top_sold_loading);
        productsSalesTableLoading = findViewById(R.id.analytics_products_sales_table_loading);
    }

    private void generateAnalyticsChart() {
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

    private void generateSalesAndSoldItems(){
        salesAndSoldReport.setVisibility(View.INVISIBLE);
        salesAndSoldLoading.setVisibility(View.VISIBLE);
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
                    salesAndSoldReport.setVisibility(View.VISIBLE);
                    salesAndSoldLoading.setVisibility(View.GONE);
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
        topSellingChart.setVisibility(View.INVISIBLE);
        topSellingLoading.setVisibility(View.VISIBLE);
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
        topSoldChart.setVisibility(View.INVISIBLE);
        topSoldLoading.setVisibility(View.VISIBLE);
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

    @SuppressLint("DefaultLocale")
    private void generateProductsSalesReport(){
        productsSalesTable.setVisibility(View.INVISIBLE);
        productsSalesTableLoading.setVisibility(View.VISIBLE);
        TestingUtils.delay(3000, ()->{
            // Header
            productsSalesTable.setHeaderAdapter(new SimpleTableHeaderAdapter(
                    this,
                    "Product",
                    "Sold Items",
                    "Sales"
            ));

            //Data
            List<ProductSalesSummaryData> productsSalesData = new ArrayList<>();
            for (int i = 0; i<30;i++){
                productsSalesData.add(new ProductSalesSummaryData(
                        String.format("id-%d", i),
                        String.format("Product %d", i),
                        i+1,
                        30-i
                ));
            }
            productsSalesTable.setDataAdapter(new ProductsSalesTableDataAdapter(this, productsSalesData));

            // Comparators
            productsSalesTable.setColumnComparator(0, Comparator.comparing(ps -> ps.productName));
            productsSalesTable.setColumnComparator(1, Comparator.comparingInt(ps -> ps.soldItems));
            productsSalesTable.setColumnComparator(2, Comparator.comparingDouble(ps -> ps.sales));

            productsSalesTable.setVisibility(View.VISIBLE);
            productsSalesTableLoading.setVisibility(View.GONE);
        });

    }

    public void navigateBack(View view){finish();}

}
