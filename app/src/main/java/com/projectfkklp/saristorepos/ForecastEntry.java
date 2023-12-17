package com.projectfkklp.saristorepos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForecastEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_entry);

        // Assuming you have initialized your UI elements
        EditText forecastOffsetEditText = findViewById(R.id.forecastOffset);
        EditText forecastSizeEditText = findViewById(R.id.forecastSize);
        Button forecastButton = findViewById(R.id.saveButton);
        Context context = this;

        // Set OnClickListener for the Forecast button
        forecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the EditText fields
                int offset = parseStringToInt(forecastOffsetEditText.getText().toString());
                int size = parseStringToInt(forecastSizeEditText.getText().toString());
                boolean valid = true;

                // Perform any necessary validation or processing here
                if (size == 0 || size > 52) {
                    forecastSizeEditText.setError("Please enter size between 1 to 52");
                    valid = false;
                }

                if (valid) {
                    Intent intent = new Intent(context, salesReport.class);
                    intent.putExtra("offset", offset);
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
