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
import com.projectfkklp.saristorepos.repositories.ReportRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;

public class DashboardSalesForecastChart extends LineChart {
    protected int actualSalesSize;
    protected int forecastSalesSize;
    protected final Date currentData = new Date();

    public DashboardSalesForecastChart(Context context) {
        super(context);
    }

    public DashboardSalesForecastChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DashboardSalesForecastChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(float[] actualSales, float[] forecastSales){
        actualSalesSize = actualSales.length;
        forecastSalesSize = forecastSales.length;
        LineDataSet actualSalesLineDataSet = createDataSet(
            "Actual Sales",
            0,
            actualSales,
            Color.rgb(89, 199, 250)
        );
        LineDataSet forecastSalesLineDataSet = createDataSet(
            "Forecast Sales",
            ReportRepository.DATA_COUNT -forecastSalesSize,
            forecastSales,
            Color.rgb(250, 104, 104)
        );
        LineData lineData = new LineData(forecastSalesLineDataSet, actualSalesLineDataSet);

        setupChart(lineData);
    }

    protected void setupChart(LineData data) {
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
            getAxisLeft().setSpaceBottom(5);
            getAxisRight().setEnabled(false);
        }

        XAxis xAxis = getXAxis();
        {
            xAxis.setEnabled(true);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setTextSize(7);
            int labelCount =
                // Sometimes, our Arima will not be able to produce forecast
                // especially when there is not enough data
                // this will make actualSales.length != (forecastSales.length - {EXTRA_FORECAST_COUNT})
                // note: I deduct the 6, since those forecasts are for tomorrow and the following days
                // As a solution, we set aside first the {EXTRA_FORECAST_COUNT} forecast (which we add later)
                // then, we check which now produce higher counts
                // finally, that higher count will be added by 6
                Math.max(actualSalesSize, forecastSalesSize - ReportRepository.EXTRA_FORECAST_COUNT)
                // No matter what happen
                // When our Arima Model is capable to forecast
                // It will produce forecast with size {EXTRA_FORECAST_COUNT} days ahead from current date
                + ReportRepository.EXTRA_FORECAST_COUNT;
            xAxis.setLabelCount(labelCount-1);
            xAxis.setLabelRotationAngle(-45);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int datePositionFromNow = (int) (value - actualSalesSize + 1);

                    if (datePositionFromNow == 0) {
                        return "Today";
                    }
                    if (datePositionFromNow == -1) {
                        return "Yesterday";
                    }
                    if (datePositionFromNow == 1) {
                        return "Tomorrow";
                    }
                    return DateUtils.SHORT_DATE_FORMAT.format(DateUtils.addDays(currentData, datePositionFromNow));
                }

            });
        }

        // animate calls invalidate()...
        animateX(2500);
    }

    private LineDataSet createDataSet(String name, int offset, float[] values, int color) {

        ArrayList<Entry> entries = new ArrayList<>();

        for (int i=0;i<values.length;i++) {
            float x = offset+i;
            float value = values[i];
            entries.add(new Entry(x, value));
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
                    return StringUtils.formatToPesoWithMetricPrefix(value);
                }
            });
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }

        // create a data object with the data sets
        return lineDataSet;
    }
}
