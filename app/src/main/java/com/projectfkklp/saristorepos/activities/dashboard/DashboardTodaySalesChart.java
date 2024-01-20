package com.projectfkklp.saristorepos.activities.dashboard;

import android.annotation.SuppressLint;
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
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardTodaySalesChart extends PieChart {
    private String title;
    private ArrayList<PieEntry> pieEntries;

    public DashboardTodaySalesChart(Context context) {  super(context); }

    public DashboardTodaySalesChart(Context context, AttributeSet attrs) {  super(context, attrs);  }

    public DashboardTodaySalesChart(Context context, AttributeSet attrs, int defStyle) {    super(context, attrs, defStyle);    }
    public void initializeTodaySalesChart(String title) {
        this.title = title;

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

    public void setData(float yesterdaySales, float todayActualSales, float todayTargetSales) {

        float salesPerformancePercentage = todayActualSales / todayTargetSales * 100;
        float salesGrowthPercentage = (todayActualSales - yesterdaySales)/yesterdaySales * 100;
        float remainingTargetSales = Math.max(0, todayTargetSales-todayActualSales);

        // Pie Chart Entries
        {
            pieEntries = new ArrayList<>(Arrays.asList(
                new PieEntry(todayActualSales),
                new PieEntry(remainingTargetSales)
            ));
            PieDataSet dataSet = new PieDataSet(pieEntries, title);
            dataSet.setDrawValues(false);
            dataSet.setColors(
                Color.parseColor("#0096FF"),
                Color.parseColor("#A9A9A9")
            );
            PieData data = new PieData(dataSet);
            {
                data.setValueTextSize(7f);
                data.setValueTextColor(Color.BLACK);
            }
            setData(data);
        }

        SpannableString spannableString = generateCenterText(todayActualSales, todayTargetSales,salesPerformancePercentage, salesGrowthPercentage);
        setCenterText(spannableString);

        animateY(1400, Easing.EaseInOutQuad);
        invalidate();
    }

    // Method to generate customizable center text
    private SpannableString generateCenterText(float todayActualSales, float todayTargetSales, float overallSalesPercentage, float salesGrowthPercentage) {

        @SuppressLint("DefaultLocale") String centerText = String.format(
            "Summary\n"
                +"%.0f%% to Goal\n"
                +"Actual Sales: %s\n"
                +"Target Sales: %s\n"
                +"+%.2f%% vs Yesterday",
            overallSalesPercentage,
            StringUtils.formatPesoPrefix(todayActualSales),
            StringUtils.formatPesoPrefix(todayTargetSales),
            salesGrowthPercentage
        );

        SpannableString spannableString = new SpannableString(centerText);
        {// Change the color of "Today Sales" and "10%" to green
            int blueColor = Color.parseColor("#0000FF");
            int greenColor = Color.parseColor("#40C94F");
            int crimsonColor = Color.parseColor("#DC143C");

            spannableString.setSpan(new ForegroundColorSpan(blueColor), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
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

}
