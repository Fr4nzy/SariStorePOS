package com.projectfkklp.saristorepos.activities.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.utils.DateUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class DashboardSalesLineChart extends LineChart {
    private int actualSalesSize;
    private int forecastSalesSize;
    private final Date currentData = new Date();

    public DashboardSalesLineChart(Context context) {
        super(context);
    }

    public DashboardSalesLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardSalesLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(float[] actualSales, float[] forecastSales){
        actualSalesSize = actualSales.length;
        forecastSalesSize = forecastSales.length;
        LineDataSet actualSalesLineDataSet = createDataSet("Actual Sales", actualSales, Color.rgb(89, 199, 250));
        LineDataSet forecastSalesLineDataSet = createDataSet("Forecast Sales", forecastSales, Color.rgb(250, 104, 104));
        LineData lineData = new LineData(actualSalesLineDataSet, forecastSalesLineDataSet);

        setupChart(lineData);
    }

    private void setupChart(LineData data) {
        // General Configurations
        {
            ((LineDataSet) data.getDataSetByIndex(0)).setCircleHoleColor(Color.WHITE);
            getDescription().setEnabled(false);
            setDrawGridBackground(false);
            setTouchEnabled(false);
            setDragEnabled(false);
            setScaleEnabled(false);
            setPinchZoom(false);

            setBackgroundColor(Color.WHITE);
        }

        // add data
        setData(data);

        // get the legend (only possible after setting data)
        Legend l = getLegend();
        l.setEnabled(true);

        // YAxis
        {
            getAxisLeft().setEnabled(false);
            getAxisLeft().setSpaceTop(15);
            getAxisLeft().setSpaceBottom(10);
            getAxisRight().setEnabled(false);
        }

        XAxis xAxis = getXAxis();
        {
            xAxis.setEnabled(true);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextSize(7);
            xAxis.setLabelCount(forecastSalesSize-1);
            xAxis.setLabelRotationAngle(-45);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int datePositionFromNow = (int)(value - actualSalesSize + 1);

                    if (datePositionFromNow==0){
                        return "Today";
                    }
                    return DateUtils.SHORT_DATE_FORMAT.format(DateUtils.addDays(currentData, datePositionFromNow));
                }
            });
        }

        // animate calls invalidate()...
        animateX(2500);
    }

    private LineDataSet createDataSet(String name, float[] values, int color) {

        ArrayList<Entry> entries = new ArrayList<>();

        for (int i=0;i<values.length;i++) {
            float value = values[i];
            entries.add(new Entry(i, value));
        }

        // create a dataset and give it a type
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        {
            lineDataSet.setLineWidth(1.75f);
            lineDataSet.setCircleRadius(5f);
            lineDataSet.setCircleHoleRadius(2.5f);
            lineDataSet.setColor(color);
            lineDataSet.setCircleColor(color);
            lineDataSet.setHighLightColor(color);
            lineDataSet.setDrawValues(true);
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    // Displaying sales with a currency symbol
                    return StringUtils.formatToPeso(value);
                }
            });
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }

        // create a data object with the data sets
        return lineDataSet;
    }
}
