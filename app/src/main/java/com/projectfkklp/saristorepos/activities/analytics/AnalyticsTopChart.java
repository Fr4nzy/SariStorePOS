package com.projectfkklp.saristorepos.activities.analytics;

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

public class AnalyticsTopChart extends PieChart {
    private String title;
    private final int TOP_COUNT = 5;
    private ArrayList<PieEntry> pieEntries;
    public AnalyticsTopChart(Context context) {
        super(context);
    }

    public AnalyticsTopChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnalyticsTopChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initializePieChart(String title){
        this.title = title;

        setDrawHoleEnabled(false);
        getLegend().setEnabled(false);
        getDescription().setEnabled(false);
        setMinAngleForSlices(35f);


        setEntryLabelColor(Color.BLACK);
        setEntryLabelTextSize(11f);
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

    private void setPieEntries(HashMap<String, Integer> entriesHash){
        pieEntries = new ArrayList<>();

        ArrayList<Pair<String, Integer>> entryPairs = new ArrayList<>();
        for (String key : entriesHash.keySet()) {
            int value = entriesHash.get(key);
            entryPairs.add(new Pair<>(key, value));
        }

        // Sort by descending order to get the highest selling products
        entryPairs.sort(Comparator.comparing(pair -> -pair.second));

        // Add top items
        int topCount = Math.min(TOP_COUNT, entryPairs.size());
        for (int i=0; i<topCount; i++){
            Pair<String, Integer> entryPair = entryPairs.get(i);
            pieEntries.add(new PieEntry(entryPair.second, entryPair.first));
        }
    }

}