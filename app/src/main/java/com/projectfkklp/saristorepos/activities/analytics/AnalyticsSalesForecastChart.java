package com.projectfkklp.saristorepos.activities.analytics;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardSalesForecastChart;
import com.projectfkklp.saristorepos.repositories.ReportRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;

public class AnalyticsSalesForecastChart extends DashboardSalesForecastChart {

    public AnalyticsSalesForecastChart(Context context) {
        super(context);
    }

    public AnalyticsSalesForecastChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnalyticsSalesForecastChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void setupChart(LineData data) {
        // General Configurations
        {
            ((LineDataSet) data.getDataSetByIndex(0)).setCircleHoleColor(Color.WHITE);
            getDescription().setEnabled(false);
            setBackgroundColor(Color.WHITE);
        }
        // add data
        setData(data);

        // get the legend (only possible after setting data)
        Legend l = getLegend();
        l.setEnabled(true);

        // YAxis
        {
            getAxisLeft().setSpaceTop(15);
            getAxisLeft().setSpaceBottom(5);
            getAxisLeft().setTextSize(7);
            getAxisRight().setEnabled(false);
        }

        XAxis xAxis = getXAxis();
        {
            xAxis.setEnabled(true);
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
}
