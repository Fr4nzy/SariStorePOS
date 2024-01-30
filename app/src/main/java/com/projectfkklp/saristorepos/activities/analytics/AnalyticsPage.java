package com.projectfkklp.saristorepos.activities.analytics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private AnalyticsSalesForecastChart analyticsChart;
    private SwipeRefreshLayout swipeRefresh;
    private AnalyticsTodaySalesChart todaySalesChart;
    private AnalyticsTopChart topSellingChart;
    private AnalyticsTopChart topSoldChart;
    private SortableTableView<ProductSalesSummaryData> productsSalesTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics_page);

        initializeViews();
        generateCharts();

    }

    private void generateCharts(){
        generateAnalyticsChart();
        generateTodaySalesChart();
        generateTopSellingChart();
        generateTopSoldChart();
        generateProductsSalesReport();
    }

    private void initializeViews() {
        swipeRefresh = findViewById(R.id.analytics_swipe_refresh);
        analyticsChart = findViewById(R.id.analytics_forecast_chart);

        todaySalesChart = findViewById(R.id.analytics_today_sales_chart);
        topSellingChart = findViewById(R.id.analytics_top_selling_chart);
        topSoldChart = findViewById(R.id.analytics_top_sold_chart);

        productsSalesTable = findViewById(R.id.analytics_products_sales_table);

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
        });

        todaySalesChart.initializeTodaySalesChart("Today Sales");
        topSellingChart.initializePieChart("Top Selling Product");
        topSoldChart.initializePieChart("Top Sold Products");
    }

    private void generateAnalyticsChart() {
        float[] actualSales = generateRandomDoubleArray(7);
        float[] forecastSales = generateRandomDoubleArray(10);
        analyticsChart.setData(actualSales, forecastSales);
    }

    private void generateTodaySalesChart() {
        // Show loading and hide chart
        todaySalesChart.setVisibility(View.INVISIBLE);

        TestingUtils.delay(1500, ()->{
            float ySales = 4_545_454_545.454545F;
            float taSales = 5_000_000_000F;
            float ttSales = 100_000_000_00f;
            todaySalesChart.setData(ySales, taSales, ttSales);
            // Hide loading and show chart
            todaySalesChart.setVisibility(View.VISIBLE);
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

    @SuppressLint("DefaultLocale")
    private void generateProductsSalesReport(){
        // Header
        productsSalesTable.setHeaderAdapter(new SimpleTableHeaderAdapter(
            this,
            "Product",
            "Sold Items",
            "Sales"
        ));

        // Data
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
    }

    private float[] generateRandomDoubleArray(int count) {
        float[] values = new float[count];

        for (int i = 0; i < count; i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }
    public void navigateBack(View view){finish();}

}
