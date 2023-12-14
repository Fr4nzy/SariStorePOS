package com.example.saristorepos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView viewInventory, viewPOS, salesReport, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewInventory = findViewById(R.id.viewInventory);
        viewPOS = findViewById(R.id.viewPOS);
        salesReport = findViewById(R.id.salesReport);
        logout = findViewById(R.id.logout);

        viewInventory.setOnClickListener(this);
        viewPOS.setOnClickListener(this);
        salesReport.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;

        if (view.getId() == R.id.viewInventory) {
            i = new Intent(this, viewInventory.class); // Assuming ViewInventory is the correct class name
        } else if (view.getId() == R.id.viewPOS) {
            i = new Intent(this, viewPOS.class); // Assuming ViewPOS is the correct class name
        } else if (view.getId() == R.id.salesReport) {
            i = new Intent(this, ForecastEntry.class); // Assuming SalesReport is the correct class name
        } else if (view.getId() == R.id.logout) {
            i = new Intent(this, login.class); // Assuming Login is the correct class name
        } else {
            return; // Do nothing for other cases
        }

        startActivity(i);
    }
}