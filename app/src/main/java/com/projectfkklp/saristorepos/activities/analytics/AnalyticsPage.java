package com.projectfkklp.saristorepos.activities.analytics;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.TestingUtils;

import java.text.DecimalFormat;
import java.util.HashMap;

public class AnalyticsPage extends AppCompatActivity {
    private AnalyticsSalesForecastChart analyticsChart;
    private SwipeRefreshLayout swipeRefresh;
    private AnalyticsTodaySalesChart todaySalesChart;
    private AnalyticsTopChart topSellingChart;
    private AnalyticsTopChart topSoldChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_page);

        initializeViews();
        generateCharts();

    }

    private void generateCharts(){
        generateAnalyticsChart();
        generateTodaySalesChart();
        generateTopSellingChart();
        generateTopSoldChart();
    }

    private void initializeViews() {
        swipeRefresh = findViewById(R.id.analytics_swipe_refresh);
        analyticsChart = findViewById(R.id.analytics_forecast_chart);

        todaySalesChart = findViewById(R.id.analytics_today_sales_chart);
        topSellingChart = findViewById(R.id.analytics_top_selling_chart);
        topSoldChart = findViewById(R.id.analytics_top_sold_chart);

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

    private float[] generateRandomDoubleArray(int count) {
        float[] values = new float[count];

        for (int i = 0; i < count; i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }
    public void navigateBack(View view){finish();}

}
