package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Pair;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class DashboardTopPieChart extends PieChart {
    private String title;
    private String salesTitle;
    private ArrayList<PieEntry> pieEntries;
    private ArrayList<PieEntry> salesPieEntries;
    public DashboardTopPieChart(Context context) {
        super(context);
    }

    public DashboardTopPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardTopPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initializePieChart(String title){
        this.title = title;

        setDrawHoleEnabled(false);
        getLegend().setEnabled(false);

        getDescription().setEnabled(false);

        setEntryLabelColor(Color.BLACK);
        setEntryLabelTextSize(9f);
        setDragDecelerationFrictionCoef(0.95f);
    }

    public void initializeSalesPieChart(String salesTitle){
        this.salesTitle = salesTitle;

        setDrawHoleEnabled(true);
        setHoleRadius(70f); // To Reduce the size of the Slice
        setHoleColor(Color.TRANSPARENT); // Set hole color as transparent
        setTransparentCircleColor(Color.TRANSPARENT); // Set transparent circle color
        setTransparentCircleRadius(20f); // Adjust the radius of the transparent circle
        setDrawCenterText(true); // Enable drawing center text
        // Disable the labels inside the chart's cells
        setDrawEntryLabels(false);

        getLegend().setEnabled(false);
        getDescription().setEnabled(false);

        setEntryLabelColor(Color.BLACK);
        setEntryLabelTextSize(7f);
        setDragDecelerationFrictionCoef(0.95f);
    }

    public void setData(HashMap<String, Integer> entriesHash, ValueFormatter valueFormatter) {
        setPieEntries(entriesHash);
        PieDataSet dataSet = new PieDataSet(pieEntries, title);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

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


    private void setSalesPieEntries(HashMap<String, Double> salesEntriesHash){
        salesPieEntries = new ArrayList<>();

        for (String key : salesEntriesHash.keySet()) {
            double value = salesEntriesHash.get(key);
            salesPieEntries.add(new PieEntry((float) value, key));
        }
    }

    private void setPieEntries(HashMap<String, Integer> entriesHash){
        pieEntries = new ArrayList<>();

        ArrayList<Pair<String, Integer>> entryPairs = new ArrayList<>();
        for (String key : entriesHash.keySet()) {
            int value = entriesHash.get(key);
            entryPairs.add(new Pair<>(key, value));
        }

        // Sort by descending order to get the highest selling products
        entryPairs.sort(Comparator.comparing(pair -> -pair.second));

        // Add top three first
        for (int i=0; i<3; i++){
            Pair<String, Integer> entryPair = entryPairs.get(i);
            pieEntries.add(new PieEntry(entryPair.second, entryPair.first));
        }

        // Compute others
        int others = 0;
        for (int i=3; i<entryPairs.size(); i++){
            Pair<String, Integer> entryPair = entryPairs.get(i);
            others += entryPair.second;
        }
        if (others>0){
            pieEntries.add(new PieEntry(others, "Others"));
        }
    }

}
