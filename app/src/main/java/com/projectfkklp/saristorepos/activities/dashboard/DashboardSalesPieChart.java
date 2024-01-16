package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class DashboardSalesPieChart extends PieChart {

    private String salesTitle;
    private ArrayList<PieEntry> salesPieEntries;

    public DashboardSalesPieChart(Context context) {
        super(context);
    }

    public DashboardSalesPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardSalesPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initializedSalesPieChart(String salesTitle) {
        this.salesTitle = salesTitle;





    }

}
