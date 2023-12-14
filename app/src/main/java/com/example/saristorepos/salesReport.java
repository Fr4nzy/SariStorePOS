package com.example.saristorepos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.Datasets;
import com.workday.insights.timeseries.arima.struct.ForecastResult;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.AxisBase;

import java.util.ArrayList;
import java.util.List;

public class salesReport extends AppCompatActivity {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        // Prepare forecast data
        Intent intent = getIntent();
        int offset = intent.getIntExtra("offset", 0);
        int size = intent.getIntExtra("size", 52);

        ForecastResult forecastResult = Arima.forecast(offset + size, this);
        double[] forecastData = forecastResult.getForecast();
        double[] actualData = Datasets.loadTestingDatasets(this);

        // Line Chart Start
        LineChart lineChart = findViewById(R.id.chart);

        // Set empty description
        Description salesReport = new Description();
        salesReport.setText("Sales Report");
        salesReport.setTextSize(18f);
        lineChart.setDescription(salesReport);

        // Set custom X-axis formatter
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DayAxisValueFormatter()); // Custom formatter for days

        // Set custom Y-axis formatter
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setValueFormatter(new SalesAxisValueFormatter()); // Custom formatter for sales

        // Disable the right y-axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        List<Entry> forecastEntries = new ArrayList<>();
        List<Entry> actualEntries = new ArrayList<>();

        // Adjust X values for forecasted data to appear at the end of the actual dataset line
        for (int i = 1; i <= actualData.length; i++) {
            actualEntries.add(new Entry((float) i, (float) actualData[i - 1]));
        }

        for (int i = actualData.length + 1; i <= actualData.length + forecastData.length; i++) {
            forecastEntries.add(new Entry((float) i, (float) forecastData[i - actualData.length - 1]));
        }

        LineDataSet forecastDataset = new LineDataSet(forecastEntries, "Forecast");
        forecastDataset.setColor(Color.RED);

        LineDataSet actualDataset = new LineDataSet(actualEntries, "Actual");
        actualDataset.setColor(Color.BLUE);

        LineData lineData = new LineData(forecastDataset, actualDataset);

        lineChart.setData(lineData);

        lineChart.invalidate();
    }

    // Custom X-axis value formatter for days
    private static class DayAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "Day " + (int) value; // Displaying day as integer
        }
    }

    // Custom Y-axis value formatter for sales
    private static class SalesAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "₱" + (int) value; // Displaying sales with a currency symbol
        }
    }
}