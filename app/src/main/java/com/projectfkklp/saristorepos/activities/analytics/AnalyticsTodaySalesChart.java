package com.projectfkklp.saristorepos.activities.analytics;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class AnalyticsTodaySalesChart extends PieChart {
    private String title;
    private ArrayList<PieEntry> pieEntries;

    public AnalyticsTodaySalesChart(Context context) {  super(context); }

    public AnalyticsTodaySalesChart(Context context, AttributeSet attrs) {  super(context, attrs);  }

    public AnalyticsTodaySalesChart(Context context, AttributeSet attrs, int defStyle) {    super(context, attrs, defStyle);    }
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
        setCenterTextSize(12f);
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
    private SpannableString generateCenterText(float todayActualSales, float todayTargetSales, float salesPerformancePercentage, float salesGrowthPercentage) {
        DecimalFormat decimalFormat = new DecimalFormat("+#,##0.00;-#");
        String salesGrowthPercentageText = decimalFormat.format(salesGrowthPercentage);

        @SuppressLint("DefaultLocale") String centerText = String.format(
                "Summary\n"
                        +"%.0f%% to Goal\n"
                        +"Actual Sales: %s\n"
                        +"Target Sales: %s\n"
                        +"%s%% vs Yesterday",
                salesPerformancePercentage,
                StringUtils.formatToPeso(todayActualSales),
                StringUtils.formatToPeso(todayTargetSales),
                salesGrowthPercentageText
        );

        SpannableString spannableString = new SpannableString(centerText);

        // Change the color and style of "Summary" to blue and bold
        int blueColor = Color.parseColor("#0000FF");
        int titleStartIndex = 0;
        int titleEndIndex = 7;
        spannableString.setSpan(new ForegroundColorSpan(blueColor), titleStartIndex, titleEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), titleStartIndex, titleEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Change the color of "%.0f%% to Goal" to green
        int greenColor = Color.parseColor("#40C94F");
        int salesPerformancePercentageStartIndex = titleEndIndex;
        int salesPerformancePercentageEndIndex = centerText.indexOf(" to Goal");
        spannableString.setSpan(new ForegroundColorSpan(greenColor), salesPerformancePercentageStartIndex, salesPerformancePercentageEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Determine the color for "%.0f%% vs Yesterday" based on growth
        int salesGrowthPercentageStartIndex = centerText.indexOf(salesGrowthPercentageText);
        int salesGrowthPercentageEndIndex = centerText.indexOf(" vs Yesterday");
        // Change the color to green if there is growth, otherwise crimson
        int growthColor = (salesGrowthPercentage >= 0) ? Color.parseColor("#40C94F") : Color.parseColor("#DC143C");
        spannableString.setSpan(new ForegroundColorSpan(growthColor), salesGrowthPercentageStartIndex, salesGrowthPercentageEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

}
