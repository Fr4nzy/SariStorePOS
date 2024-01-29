package com.projectfkklp.saristorepos.activities.super_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.utils.Arima;

import java.util.HashMap;
import java.util.List;


public class ArimaEvaluationPage extends AppCompatActivity {
    EditText evaluationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arima_evaluation_page);

        evaluationText = findViewById(R.id.arima_evaluation_textarea);

        Arima.evaluate(this, (actualSales, forecastSales)->{
            HashMap<String, List<Double>> evaluationData = new HashMap<>();

            evaluationData.put("actualSales", actualSales);
            evaluationData.put("forecastSales", forecastSales);

            Gson gson = new Gson();
            String json = gson.toJson(evaluationData);

            evaluationText.setText(json);
        });
    }

    public void navigateBack(View view){
        finish();
    }
}