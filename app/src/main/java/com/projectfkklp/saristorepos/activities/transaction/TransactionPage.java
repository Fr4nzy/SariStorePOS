package com.projectfkklp.saristorepos.activities.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.DailyTransactions;

import java.util.Arrays;
import java.util.List;

public class TransactionPage extends AppCompatActivity {
    Spinner modeDropdown;
    RecyclerView recyclerView;

    List<DailyTransactions> dailyTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_page);

        loadDailyTransactions();
        initializeViews();
    }

    private void loadDailyTransactions(){
        dailyTransactions = Arrays.asList(
            new DailyTransactions(),
            new DailyTransactions()
        );
    }

    private void initializeViews(){
        modeDropdown = findViewById(R.id.transaction_dropdown);
        recyclerView = findViewById(R.id.transaction_recycler);

        initializeDropdown();
    }

    private void initializeDropdown(){
        List<String> items = Arrays.asList("Daily Summarized", "Not Summarized");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeDropdown.setAdapter(adapter);

        modeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                String selectedItem = (String) parentView.getItemAtPosition(position);
                int selectedItemIndex = items.indexOf(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here, or handle the case where nothing is selected
            }
        });
    }

    public void navigateBack(View view){
        finish();
    }

}