package com.workday.insights.timeseries.arima;
import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Several sample datasets to test with each of these retruns a hash<string, double> because this
 * allows for the general case when data is not evenly spaced or is missing and we have to impute
 * before having a nice array to work with.  Each type of key will have to be mapped to some sort of
 * numbered index before we can actually compute with it.
 */
public final class Datasets {

    private static final String JSON_FILE = "datasets_weekly.json";
    private static final double TRAINING_SIZE = 0.75; // in percent

    public static double[] loadDatasets(Context context ) {
        double[] result = new double[]{};
        try {
            // Open the JSON file from assets
            InputStream inputStream = context.getAssets().open(JSON_FILE);

            // Read the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Parse the JSON data
            String jsonData = builder.toString();

            // Now you can work with the jsonObject
            Gson gson = new Gson();
            result = gson.fromJson(jsonData, double[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double[] loadTrainingDatasets(Context context) {
        double[] originalArray = loadDatasets(context);
        int subsetSize = (int) Math.ceil(TRAINING_SIZE * originalArray.length);
        return Arrays.copyOfRange(originalArray, 0, subsetSize);
    }

    public static double[] loadTestingDatasets(Context context) {
        double[] originalArray = loadDatasets(context);
        double testingSizeInPercent = 1 - TRAINING_SIZE;
        int from = (int) Math.ceil(TRAINING_SIZE * originalArray.length);
        int subsetSize = (int) Math.floor(testingSizeInPercent * originalArray.length);
        return Arrays.copyOfRange(originalArray, from, from+subsetSize);
    }

}
