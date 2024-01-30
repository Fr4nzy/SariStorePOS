package com.projectfkklp.saristorepos.activities.analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary._TransactionDailySummaryPage;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.models._DailySalesSummary;
import com.projectfkklp.saristorepos.repositories.TransactionRepository;

public class AnalyticsDailySalesPage extends AppCompatActivity {
    public List<_Transaction> transactionList;
    public List<_DailySalesSummary> summaryList;
    SimpleDateFormat mFormat = new SimpleDateFormat("MMM dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._analytics_daily_sales_page);

        summaryList = new ArrayList<>();
        try {
            TransactionRepository.retrieveAllTransactions((transactions) -> {
                transactionList=transactions;
                showSalesReport();
            }, this);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showSalesReport() throws ParseException {
        _TransactionDailySummaryPage.setSummaryList(transactionList, summaryList);

        // Line Chart Start
        LineChart lineChart = findViewById(R.id.linechart);

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

        List<Entry> salesEntries = new ArrayList<>();

        // Adjust X values for forecasted data to appear at the end of the actual dataset line
        for (int i = 0; i < summaryList.size(); i++) {
            salesEntries.add(new Entry((float) i, (float) summaryList.get(i).getTotalPrice()));
        }

        LineDataSet forecastDataset = new LineDataSet(salesEntries, "Sales of Day");
        forecastDataset.setColor(Color.RED);

        LineData lineData = new LineData(forecastDataset);

        lineChart.setData(lineData);

        lineChart.invalidate();
    }

     class DayAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(summaryList.get((int)value).getDate());
        }
    }

    static class SalesAxisValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "₱" + (int) value; // Displaying sales with a currency symbol
        }
    }

}