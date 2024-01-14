package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.google.gson.Gson;
import com.projectfkklp.saristorepos.interfaces.OnDatasetRetrieved;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class DatasetRepository {

    private static final String JSON_FILE = "daily_sales.json";

    public static void getDatasetFromFirebase(OnDatasetRetrieved onDatasetRetrieved) {
        UserRepository.getUserByCurrentAuthentication(user->{
            List<Double> dailySales = user.getDailySales();
            Date updatedAt = user.getDailySalesUpdatedAt();
            Date currentDate = new Date();

            // get gaps for difference of two or more, and fill it with zeros
            long gaps = DateUtils.calculateDaysDifference(updatedAt, currentDate) - 1;
            for (int i=0; i<gaps;i++){
                dailySales.add(0.);
            }

            double[] dataset = dailySales.stream().mapToDouble(Double::doubleValue).toArray();
            onDatasetRetrieved.onDatasetRetrieved(dataset);
        });
    }

    public static double[] getDatasetFromJson(Context context) {
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

    private static int getTrainingDatasetCount(int datasetSize) {
        return datasetSize-365;
    }

    public static double[] getTrainingDataset(Context context) {
        // Get all data and then return a subset for training
        double[] allDataset = getDatasetFromJson(context);

        int trainingSize = getTrainingDatasetCount(allDataset.length);

        // Return a copy of the subset
        return java.util.Arrays.copyOf(allDataset, trainingSize);
    }
}
