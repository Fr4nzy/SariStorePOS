package com.projectfkklp.saristorepos.activities.analytics;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.dashboard.DashboardSalesForecastChart;

public class AnalyticsPage extends AppCompatActivity {
    private DashboardSalesForecastChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_page);

        chart = findViewById(R.id.dashboard_analytics_chart);
        applyDefaultZoom();
    }

    private void applyDefaultZoom() {
        float defaultZoomLevel = 1.5f;

        chart.setScaleX(defaultZoomLevel);
        chart.setScaleY(defaultZoomLevel);
    }
}
