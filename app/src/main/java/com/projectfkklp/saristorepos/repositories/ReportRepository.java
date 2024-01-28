package com.projectfkklp.saristorepos.repositories;

import android.content.Context;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.projectfkklp.saristorepos.utils.Arima;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportRepository {
    public static final int ACTUAL_SALES_COUNT = 7;
    public static final int EXTRA_FORECAST_COUNT = 6;
    public static final int DATA_COUNT = ACTUAL_SALES_COUNT + EXTRA_FORECAST_COUNT;

    public static Task<Task<Pair<List<Double>, List<Double>>>> getRecentSalesAndForecastWithHistoryReport(Context context){
        return StoreRepository.getStoreById(SessionRepository.getCurrentStore(context).getId())
            .continueWith(task->{
                Store store = task.getResult().toObject(Store.class);
                assert store != null;

                // Set dailySales (input data)
                List<Double> dailySales = store.getDailySales();
                List<Double> recentDailySales;
                List<Double> forecastData;
                Date lastUpdatedAt = store.getDailySalesUpdatedAt();
                {
                    Date currentDate = new Date();
                    long daysSinceLastUpdate  = DateUtils.calculateDaysDifference(lastUpdatedAt, currentDate);

                    for (int i=0; i<daysSinceLastUpdate;i++){
                        dailySales.add(0.);
                    }

                    // Get Recent Daily Sales (including today sale)
                    recentDailySales = dailySales.subList(
                        Math.max(dailySales.size(), ACTUAL_SALES_COUNT)-ACTUAL_SALES_COUNT,
                        dailySales.size()
                    );

                    // If no transactions yet, do not proceed
                    if (recentDailySales.isEmpty()){
                        return Tasks.forResult(new Pair<>(new ArrayList<>(),new ArrayList<>()));
                    }
                }

                // Remove sales today before feeding to Arima model
                forecastData = dailySales.subList(0, dailySales.size()-1);

                // Initialize forecastWithHistory (output data)
                List<Double> forecastWithHistory = new ArrayList<>();

                // Get Forecast History (from 7 days ago to yesterday)
                int end = Math.min(ACTUAL_SALES_COUNT, forecastData.size());
                for (int i=2; i<=end;i++){
                    try{
                        double[] sublist = forecastData
                            .subList(0, forecastData.size()-i)
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray();

                        double[] result = Arima.forecast(
                            1,
                            sublist
                        );

                        forecastWithHistory.add(0, result[0]);
                    }
                    catch (Exception ignored){}
                }

                // Get forecast for today and to the next 7 days
                {
                    double[] forecastData_ = forecastData
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .toArray();
                    try{
                        double[] forecastResults = Arima.forecast(7, forecastData_);
                        for (double result : forecastResults) {
                            forecastWithHistory.add(result);
                        }
                    }
                    catch (Exception ignored){}
                }

                return Tasks.forResult(new Pair<>(recentDailySales,forecastWithHistory));
            })
        ;
    }
}
