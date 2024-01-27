package com.projectfkklp.saristorepos.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.projectfkklp.saristorepos.interfaces.OnEvaluateArimaModel;
import com.projectfkklp.saristorepos.interfaces.OnForecastResultRetrieved;
import com.projectfkklp.saristorepos.repositories.DatasetRepository;
import com.workday.insights.timeseries.arima.ArimaSolver;
import com.workday.insights.timeseries.arima.struct.ArimaModel;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Collections;


/**
 * ARIMA implementation
 */
public final class Arima {
    public static ArimaModel createModel(double[] forecastData) {
        // try seasonal periods from yearly, monthly, to weekly
        // the more data you have, you can use higher value and better forecast result
        int[] seasonalPeriods = {
            365,
            28,
            7
        };

        for (int seasonalPeriod: seasonalPeriods){
            try {
                // Attempt to create a model with the original forecast data
                return createModel(forecastData, seasonalPeriod);
            } catch (final Exception ex) {
                // If creating a model with the original data fails, try with an augmented dataset
                try {
                    double[] augmentedForecastData = augmentDataset(forecastData);
                    return createModel(augmentedForecastData, seasonalPeriod);
                } catch (Exception augEx) {
                    // If creating a model with the augmented data also fails, handle or log the exception
                    // You can add logging or other error-handling mechanisms here if needed
                    augEx.printStackTrace();
                }
            }
        }

        throw new RuntimeException("Failed to build ARIMA Model");
    }

    public static double[] augmentDataset(double[] rawDataset){
        // Get virtual dail
        Random random = new Random();
        List<Double> virtualDataset = new ArrayList<>();
        for (int index = 0; index < rawDataset.length; index++) {
            int reversedIndex = (rawDataset.length - 1) - index;
            double rawData = rawDataset[reversedIndex];

            double randomFactor = random.nextDouble() * 0.1 + 0.9;
            double powerFactor = Math.pow(1.1, (double) index / 365);

            double virtualSale = (int) (rawData * randomFactor + powerFactor);
            virtualDataset.add(virtualSale);
        }
        Collections.reverse(virtualDataset);

        double[] augmentedDataset = new double[rawDataset.length*2];
        for (int i=0;i<virtualDataset.size();i++){
            augmentedDataset[i]=virtualDataset.get(i);
            augmentedDataset[rawDataset.length+i] = rawDataset[i];
        }
        return augmentedDataset;
    }

    public static ArimaModel createModel(double[] forecastData, int seasonalPeriod) {
        ArimaModel trainedModel;
        // Set ARIMA model parameters.
        int p = 6;
        int d = 1;
        int q = 0;
        int P = 6;
        int D = 0;
        int Q = 0;
        int m = seasonalPeriod;

        ArimaParams arimaParams = new ArimaParams(p, d, q, P, D, Q, m);

        // return Trained Model if successes
        trainedModel = ArimaSolver.estimateARIMA(
            arimaParams,
            forecastData,
            forecastData.length,
            forecastData.length + 1
        );

        return trainedModel;
    }

    public static double[] forecast(final int forecastSize, double[] dataset){
        ArimaModel model = createModel(dataset);
        ForecastResult result = model.forecast(forecastSize);
        return result.getForecast();
    }

    public static void forecast(final int forecastSize, OnForecastResultRetrieved onRetrieved) throws ParseException {
        DatasetRepository.getDatasetFromFirebase(dataset -> {
            ArimaModel model = createModel(dataset);
            ForecastResult result = model.forecast(forecastSize);
            onRetrieved.onForecastResultRetrieved(result);
        });
    }

    public static void evaluate(Context context, OnEvaluateArimaModel onEvaluate){
        double[] dataset = DatasetRepository.getDatasetFromJson(context);
        int datasetLength = dataset.length;
        int testSize = 371; // 365 + 6 for forecast history
        int b = 0;
        int n=testSize-b;

        ArrayList<Double> actualSales = new ArrayList<>();
        ArrayList<Double> forecastSales = new ArrayList<>();

        for (double actualSale:Arrays.copyOfRange(dataset, datasetLength-testSize, datasetLength)) {
            actualSales.add(actualSale);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i=testSize; i>b;i--){
                    double[] trainingDataset = Arrays.copyOf(dataset, datasetLength-i);
                    ArimaModel model = createModel(trainingDataset);
                    double[] forecast = model.forecast(1).getForecast();

                    forecastSales.add(forecast[0]);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                onEvaluate.onEvaluate(actualSales, forecastSales);
            }
        }.execute();
    }
}
