package com.projectfkklp.saristorepos.activities.analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.projectfkklp.saristorepos.R;

public class AnalyticsForecastEntryPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._analytics_forecast_entry);

        // Assuming you have initialized your UI elements
        EditText forecastSizeEditText = findViewById(R.id.forecastSize);
        Button forecastButton = findViewById(R.id.saveButton);
        Context context = this;

        // Set OnClickListener for the Forecast button
        forecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the EditText fields
                int size = parseStringToInt(forecastSizeEditText.getText().toString());
                boolean valid = true;

                // Perform any necessary validation or processing here
                if (size == 0 || size > 28 ) {
                    forecastSizeEditText.setError("Please enter size between 1 to 28");
                    valid = false;
                }

                if (valid) {
                    Intent intent = new Intent(context, AnalyticsForecastPage.class);
                    intent.putExtra("size", size);
                    startActivity(intent);
                }
            }
        });
    }

    private static int parseStringToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
