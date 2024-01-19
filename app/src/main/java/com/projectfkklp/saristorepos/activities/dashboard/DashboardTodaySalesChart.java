package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardTodaySalesChart extends PieChart {
    private String salesTitle;
    private ArrayList<PieEntry> salesPieEntries;

    public DashboardTodaySalesChart(Context context) {  super(context); }

    public DashboardTodaySalesChart(Context context, AttributeSet attrs) {  super(context, attrs);  }

    public DashboardTodaySalesChart(Context context, AttributeSet attrs, int defStyle) {    super(context, attrs, defStyle);    }
    public void initializeTodaySalesChart(String salesTitle) {

        this.salesTitle = salesTitle;

        setDrawCenterText(true);
        setDrawHoleEnabled(true);
        setDrawEntryLabels(false);
        getLegend().setEnabled(false);
        getDescription().setEnabled(false);
        setHoleColor(Color.TRANSPARENT);
        setEntryLabelColor(Color.BLACK);
        setTransparentCircleColor(Color.TRANSPARENT);
        setHoleRadius(70f);
        setCenterTextSize(8f);
        setEntryLabelTextSize(7f);
        setTransparentCircleRadius(20f);
        setCenterTextOffset(0f, 0f);
        setDragDecelerationFrictionCoef(0.95f);
    }
    public void generateTodaySalesData() {
        int yesterdaySales = 10;
        int todayTargetSales = 5000;
        int todayActualSales = 3200;

        float overallSalesPercentage = (float) todayActualSales / todayTargetSales * 100;

        HashMap<String, Double> todaySalesEntries = new HashMap<>();
        todaySalesEntries.put("Actual Sales", (double) todayActualSales);
        todaySalesEntries.put("Target Sales", (double) todayTargetSales - todayActualSales);
        todaySalesEntries.put("Income from Yesterday", (double) yesterdaySales);
        salesSetData(todaySalesEntries, new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
            }
        });

        String centerText = generateCenterText(overallSalesPercentage, todayActualSales, todayTargetSales, yesterdaySales);
        setCenterText(getSpannableString(centerText));
        animateY(1400, Easing.EaseInOutQuad);
        invalidate();

    }

    // Method to generate customizable center text
    private String generateCenterText(float overallSalesPercentage, int todayActualSales, int todayTargetSales, int yesterdaySales) {
        // Change the color of specific text to green
        String centerText = String.format("Today Sales\n%.0f%% to Goal\nActual Sales: %s\nTarget Sales: %s\n\uD83D\uDCC8%d%% vs Yesterday",
                overallSalesPercentage,
                StringUtils.formatToPeso((float) todayActualSales),
                StringUtils.formatToPeso((float) (todayTargetSales - todayActualSales)),
                yesterdaySales);

        // Modify the color of "Today Sales" and "10%"
        centerText = centerText.replace("Today Sales", "Today Sales ")
                .replace("10%", "10%").replace("64%", "64%");
        return centerText;
    }

    // Method to apply SpannableString for text styling
    private SpannableString getSpannableString(String text) {
        SpannableString spannableString = new SpannableString(text);

        // Change the color of "Today Sales" and "10%" to green
        int blueColor = Color.parseColor("#0000FF");
        int greenColor = Color.parseColor("#40C94F");
        int crimsonColor = Color.parseColor("#DC143C");


        int startIndex = text.indexOf("Today Sales");
        int endIndex = startIndex + 11; // Length of "Today Sales"
        spannableString.setSpan(new ForegroundColorSpan(blueColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("10%");
        endIndex = startIndex + 3; // Length of "10%"
        spannableString.setSpan(new ForegroundColorSpan(greenColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        startIndex = text.indexOf("64%");
        endIndex = startIndex + 3; // Length of "64%"
        spannableString.setSpan(new ForegroundColorSpan(crimsonColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private void setSalesPieEntries(HashMap<String, Double> salesEntriesHash){
        salesPieEntries = new ArrayList<>();

        for (String key : salesEntriesHash.keySet()) {
            double value = salesEntriesHash.get(key);
            salesPieEntries.add(new PieEntry((float) value, key));
        }
    }

    public void salesSetData(HashMap<String, Double> salesEntriesHash, ValueFormatter valueFormatter){
        setSalesPieEntries(salesEntriesHash);

        // Set colors to gray and blue
        int grayColor = Color.parseColor("#A9A9A9");
        int blueColor = Color.parseColor("#0096FF");

        PieDataSet dataSet = new PieDataSet(salesPieEntries, salesTitle);
        dataSet.setColors(new int[]{grayColor, blueColor}); // Set colors to gray and blue

        PieData data = new PieData(dataSet);
        {
            data.setValueTextSize(7f);
            data.setValueTextColor(Color.BLACK);
            data.setValueFormatter(valueFormatter);
        }

        setData(data);
        highlightValues(null);
        animateY(1400, Easing.EaseInOutQuad);
        invalidate();
    }

}
