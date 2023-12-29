package com.projectfkklp.saristorepos.activities.analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.repositories.TransactionRepository;
import com.projectfkklp.saristorepos.Arima;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsForecastPage extends AppCompatActivity {
    public List<Transaction> transactionList;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analytics_forecast_page);

        TransactionRepository.retrieveAllTransactions((transactions) -> {
            transactionList = transactions;
            startForecast();
        }, this);
    }


    // Custom X-axis value formatter for days
    private static class DayAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            if (value == 0) {
                return "Today";
            }

            if (value == 1 ){
                return "Tomorrow";
            }

            return "Day " + (int) (value + 1); // Displaying day as integer
        }
    }

    // Custom Y-axis value formatter for sales
    private static class SalesAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "₱" + (int) value; // Displaying sales with a currency symbol
        }
    }

    private void startForecast() throws ParseException {
        Intent intent = getIntent();
        int size = intent.getIntExtra("size", 7);

        Arima.forecast( size, forecastResult->{
            try {
                double[] forecastData = forecastResult.getForecastUpperConf();

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

                // Adjust X values for forecasted data to appear at the end of the actual dataset line
                for (int i = 0; i < forecastData.length; i++) {
                    forecastEntries.add(new Entry((float) i, (float) forecastData[i]));
                }

                LineDataSet forecastDataset = new LineDataSet(forecastEntries, "Forecast");
                forecastDataset.setColor(Color.RED);

                LineData lineData = new LineData(forecastDataset);

                lineChart.setData(lineData);

                lineChart.invalidate();
            }
            catch (Exception e) {
                Toast.makeText(this, "Not enough data for forecast", Toast.LENGTH_SHORT).show();
            }
        });
    }
}