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
                // Set dailySales (input data)
                Store store = task.getResult().toObject(Store.class);
                assert store != null;
                List<Double> dailySales = store.getDailySales();
                List<Double> recentDailySales;
                List<Double> forecastData;
                {
                    Date lastUpdatedAt = store.getDailySalesUpdatedAt();
                    Date currentDate = new Date();
                    long daysSinceLastUpdate  = DateUtils.calculateDaysDifference(lastUpdatedAt, currentDate);

                    for (int i=0; i<daysSinceLastUpdate;i++){
                        dailySales.add(0.);
                    }

                    // Get Recent Daily Sales (including today sale)
                    recentDailySales = dailySales.subList(
                        dailySales.size()-ACTUAL_SALES_COUNT,
                        dailySales.size()
                    );

                    // Remove sales today before feeding to Arima model
                    if (daysSinceLastUpdate==0){
                        forecastData = dailySales.subList(0, dailySales.size()-2);
                    }
                    else {
                        forecastData = dailySales.subList(0, dailySales.size()-1);
                    }
                }

                // Initialize forecastWithHistory (output data)
                List<Double> forecastWithHistory = new ArrayList<>();

                // Get Forecast History (from 7 days ago to yesterday)
                for (int i=2; i<=ACTUAL_SALES_COUNT;i++){
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

                // Get forecast for today and to the next 7 days
                {
                    double[] forecastData_ = forecastData
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .toArray();
                    double[] forecastResults = Arima.forecast(7, forecastData_);
                    for (double result : forecastResults) {
                        forecastWithHistory.add(result);
                    }
                }

                return Tasks.forResult(new Pair<>(recentDailySales,forecastWithHistory));
            })
        ;
    }
}
