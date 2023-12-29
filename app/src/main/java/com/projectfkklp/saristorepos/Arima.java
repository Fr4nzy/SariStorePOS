package com.projectfkklp.saristorepos;

import android.content.Context;
import android.os.AsyncTask;

import com.projectfkklp.saristorepos.interfaces.OnForecastResultRetrieved;
import com.projectfkklp.saristorepos.repositories.DatasetRepository;
import com.workday.insights.timeseries.arima.ArimaSolver;
import com.workday.insights.timeseries.arima.struct.ArimaModel;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * ARIMA implementation
 */
public final class Arima {
    public static ArimaModel createModel(double[] forecastData) {
        ArimaModel trainedModel;

        // try seasonal periods from yearly, monthly, to weekly
        // the more data you have, you can use higher value and better forecast result
        int[] seasonalPeriods = {
            365,
            28,
            7
        };

        for (int seasonalPeriod: seasonalPeriods){
            try {
                // Set ARIMA model parameters.
                int p = 6;
                int d = 1;
                int q = 0;
                int P = 6;
                int D = 0;
                int Q = 0;
                int m = seasonalPeriod;

                ArimaParams arimaParams = new ArimaParams(p, d, q, P, D, Q, m);

                // return Trained Model if successed
                trainedModel = ArimaSolver.estimateARIMA(
                        arimaParams,
                        forecastData,
                        forecastData.length,
                        forecastData.length + 1
                );

                return trainedModel;
            }
            catch (final Exception ex) {
                System.out.println("Failed to build ARIMA forecast: " + ex.getMessage());
            }
        }

        throw new RuntimeException("Failed to build ARIMA forecast");
    }

    public static void forecast(final int forecastSize, OnForecastResultRetrieved onRetrieved) throws ParseException {
        DatasetRepository.getDatasetFromFirebase(dataset -> {
            ArimaModel model = createModel(dataset);
            ForecastResult result = model.forecast(forecastSize);
            onRetrieved.onForecastResultRetrieved(result);
        });
    }

    public static void evaluate(Context context){
        double[] dataset = DatasetRepository.getDatasetFromJson(context);
        int datasetLength = dataset.length;
        int a = 365;
        int b = 0;
        int n=a-b;

        ArrayList<Double> actualSales = new ArrayList<>();
        ArrayList<Double> forecastSales = new ArrayList<>();

        for (double actualSale:Arrays.copyOfRange(dataset, datasetLength-a, datasetLength)) {
            actualSales.add(actualSale);
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i=a; i>b;i--){
                    double[] trainingDataset = Arrays.copyOf(dataset, datasetLength-i);
                    ArimaModel model = createModel(trainingDataset);
                    double[] forecast = model.forecast(1).getForecast();

                    forecastSales.add(forecast[0]);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                return;
            }
        }.execute();
    }
}
