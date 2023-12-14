package com.workday.insights.timeseries.arima;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.saristorepos.MyObjectSerializer;
import com.workday.insights.timeseries.arima.struct.ArimaModel;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;


/**
 * ARIMA implementation
 */
public final class Arima {
    private static final String PREF_NAME = "ArimaModelPref";
    private static final String KEY_OBJECT = "arimaModelObject";
    private Arima() {
    } // pure static class

    private static void saveModelToSharedPreferences(ArimaModel model, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_OBJECT, MyObjectSerializer.serialize(model));
        editor.apply();
    }

    private static ArimaModel loadModelFromSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String serializedObject = prefs.getString(KEY_OBJECT, null);
        return MyObjectSerializer.deserialize(serializedObject);
    }


    public static ArimaModel setup(Context context) {
        ArimaModel trainedModel;
        try {
            // Set ARIMA model parameters.
            //p (Autoregressive Order): Start with a value between 0 and 2.
            int p = 2;
            //d (Integrated Order): Begin with 1,
            // and increase if differencing is necessary for stationary.
            int d = 1;
            //q (Moving Average Order): Start with 0 or 1.
            int q = 1;
            //P (Seasonal Autoregressive Order): Consider values between 0 and 2.
            int P = 0;
            //D (Seasonal Integrated Order): Start with 1 for weekly data, and adjust if needed.
            int D = 0;
            //Q (Seasonal Moving Average Order): Consider values between 0 and 1.
            int Q = 0;
            //m (Seasonal Period): Set this to 52 if you have weekly data, assuming a year with 52 weeks.
            int m = 0;
            ArimaParams arimaParams = new ArimaParams(p, d, q, P, D, Q, m);

            // Load Dataset
            double[] forecastData = Datasets.loadTrainingDatasets(context);

            // Train Model
            trainedModel = ArimaSolver.estimateARIMA(
                arimaParams,
                forecastData,
                forecastData.length,
                forecastData.length + 1
            );

            // Save the ARIMA model to SharedPreferences
            saveModelToSharedPreferences(trainedModel, context);
        }
        catch (final Exception ex) {
            throw new RuntimeException("Failed to build ARIMA forecast: " + ex.getMessage());
        }

        return trainedModel;
    }

    public static ForecastResult forecast(final int forecastSize, Context context) {
        // Load model
        ArimaModel model = loadModelFromSharedPreferences(context);
        if(model == null) {
            model = setup(context);
        }

        return model.forecast(forecastSize);
    }
}
