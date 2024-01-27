package com.projectfkklp.saristorepos.activities.analytics;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.projectfkklp.saristorepos.R;

public class AnalyticsPage extends AppCompatActivity {
    private AnalyticsSalesForecastChart analyticsChart;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_page);

        initializeViews();
        generateAnalyticsChart();

    }
    private void initializeViews() {
        swipeRefresh = findViewById(R.id.dashboard_swipe_refresh);
        analyticsChart = findViewById(R.id.analytics_forecast_chart);

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
        });
    }

    private void generateAnalyticsChart() {
        float[] actualSales = generateRandomDoubleArray(7);
        float[] forecastSales = generateRandomDoubleArray(10);
        analyticsChart.setData(actualSales, forecastSales);
    }

    private float[] generateRandomDoubleArray(int count) {
        float[] values = new float[count];

        for (int i = 0; i < count; i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }
}
