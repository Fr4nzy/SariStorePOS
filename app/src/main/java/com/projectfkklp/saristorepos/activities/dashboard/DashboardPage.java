package com.projectfkklp.saristorepos.activities.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;

import java.util.ArrayList;

public class DashboardPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);

        // Inflate the layout using DataBinding
        // To make it possible to pass data in reusable layout
        DataBindingUtil.setContentView(this,R.layout.dashboard_page);
    }

    public void gotoStoreSelector(View view){
        startActivity(new Intent(this, StoreSelectorPage.class));
    }
}