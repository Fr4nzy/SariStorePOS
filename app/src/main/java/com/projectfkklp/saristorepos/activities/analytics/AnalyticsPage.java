package com.projectfkklp.saristorepos.activities.analytics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.loadinganimation.LoadingAnimation;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.classes.ProductSalesSummaryData;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class AnalyticsPage extends AppCompatActivity {
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
        generateSalesAndSoldItems();
        generateTopSellingChart();
        generateTopSoldChart();
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
        TestingUtils.delay(3000, () -> {
            float[] actualSales = generateRandomDoubleArray(7);
            float[] forecastSales = generateRandomDoubleArray(10);
            analyticsChart.setData(actualSales, forecastSales);

            // Finish animation
            analyticsChart.setVisibility(View.VISIBLE);
            forecastLoading.setVisibility(View.GONE);
        });
    }

    private void generateTodaySalesChart() {
        todaySalesChart.setVisibility(View.INVISIBLE);
        todaySalesLoading.setVisibility(View.VISIBLE);
        TestingUtils.delay(3000, ()->{
            float ySales = 4_545_454_545.454545F;
            float taSales = 5_000_000_000F;
            float ttSales = 100_000_000_00f;
            todaySalesChart.setData(ySales, taSales, ttSales);

            // Hide loading and show chart
            todaySalesChart.setVisibility(View.VISIBLE);
            todaySalesLoading.setVisibility(View.GONE);
        });
    }

    private void generateSalesAndSoldItems(){
        salesAndSoldReport.setVisibility(View.INVISIBLE);
        salesAndSoldLoading.setVisibility(View.VISIBLE);
        TestingUtils.delay(3000, ()->{

            // Hide loading and show chart
            salesAndSoldReport.setVisibility(View.VISIBLE);
            salesAndSoldLoading.setVisibility(View.GONE);
        });
    }

    private void generateTopSellingChart() {
        topSellingChart.setVisibility(View.INVISIBLE);
        topSellingLoading.setVisibility(View.VISIBLE);
        TestingUtils.delay(3000, ()->{
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

            // Hide loading and show chart
            topSellingChart.setVisibility(View.VISIBLE);
            topSellingLoading.setVisibility(View.GONE);
        });
    }

    private void generateTopSoldChart() {
        topSoldChart.setVisibility(View.INVISIBLE);
        topSoldLoading.setVisibility(View.VISIBLE);
        TestingUtils.delay(3000, ()->{
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
            topSoldChart.setVisibility(View.VISIBLE);
            topSoldLoading.setVisibility(View.GONE);
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

    private float[] generateRandomDoubleArray(int count) {
        float[] values = new float[count];

        for (int i = 0; i < count; i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }
    public void navigateBack(View view){finish();}

}
