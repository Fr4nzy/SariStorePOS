package com.projectfkklp.saristorepos.activities.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;

public class DashboardPage extends AppCompatActivity {
    DashboardSalesLineChart analyticsChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);

        // Inflate the layout using DataBinding
        // To make it possible to pass data in reusable layout
        DataBindingUtil.setContentView(this,R.layout.dashboard_page);

        initializeViews();

        // Dashboard Cards
        generateAnalyticsChart();
    }

    private void initializeViews(){
        analyticsChart = findViewById(R.id.dashboard_analytics_chart);
    }

    private void generateAnalyticsChart(){
        float[] actualSales = generateRandomDoubleArray(7);
        float[] forecastSales = generateRandomDoubleArray(10);
        analyticsChart.setData(actualSales, forecastSales);
    }

    private float[] generateRandomDoubleArray(int count){
        float[] values = new float[count];

        for (int i=0;i<count;i++) values[i] = (float) (Math.random() * 2500) + 3;

        return values;
    }

    public void gotoStoreSelector(View view){
        startActivity(new Intent(this, StoreSelectorPage.class));
    }
}