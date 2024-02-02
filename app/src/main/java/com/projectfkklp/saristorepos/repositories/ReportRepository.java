package com.projectfkklp.saristorepos.repositories;

import android.content.Context;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.projectfkklp.saristorepos.classes.SalesAndSoldItemsReportData;
import com.projectfkklp.saristorepos.utils.Arima;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
                for (int i=1; i<=end;i++){
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
    public static Task<Task<List<Double>>> getTodaySalesReport(Context context){
        return StoreRepository.getStoreById(SessionRepository.getCurrentStore(context).getId())
            .continueWith(task->{
                Store store = task.getResult().toObject(Store.class);
                assert store != null;

                // Set dailySales (input data)
                List<Double> dailySales = store.getDailySales();
                Double yesterdaySales = -1.;
                Double todayActualSales = -1.;
                Double todayTargetSales = -1.;

                List<Double> forecastData;
                Date lastUpdatedAt = store.getDailySalesUpdatedAt();
                {
                    Date currentDate = new Date();
                    long daysSinceLastUpdate  = DateUtils.calculateDaysDifference(lastUpdatedAt, currentDate);

                    // Fill missing data with 0 Sales
                    for (int i=0; i<daysSinceLastUpdate;i++){
                        dailySales.add(0.);
                    }

                    // If no transactions yet, do not proceed
                    if (dailySales.isEmpty()){
                        return Tasks.forResult(new ArrayList<>(Arrays.asList(
                            yesterdaySales,
                            todayActualSales,
                            todayTargetSales
                        )));
                    }

                    // Get todayActualSales
                    todayActualSales = dailySales.get(dailySales.size()-1);

                    // Get Yesterday Sales
                    if(dailySales.size()>1){
                        yesterdaySales = dailySales.get(dailySales.size()-2);
                    }

                }

                // Get forecast for today
                {
                    // Remove sales today before feeding to Arima model
                    forecastData = dailySales.subList(0, dailySales.size()-1);

                    double[] forecastData_ = forecastData
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray();
                    try{
                        double[] forecastResults = Arima.forecast(1, forecastData_);
                        todayTargetSales = forecastResults[0];

                    }
                    catch (Exception ignored){}
                }

                    return Tasks.forResult(new ArrayList<>(Arrays.asList(
                        yesterdaySales,
                        todayActualSales,
                        todayTargetSales
                    )));
            });
    }

    public static  Task<Task<SalesAndSoldItemsReportData>> getSalesAndSoldItemsReport(Context context){
        return StoreRepository.getStoreById(SessionRepository.getCurrentStore(context).getId())
                .continueWith(task->{
                    Store store = task.getResult().toObject(Store.class);
                    assert store != null;

                    // Set dailySales
                    List<Double> dailySales = store.getDailySales();
                    List<Integer> dailySold = store.getDailySold();
                    Date lastUpdatedAt = store.getDailySalesUpdatedAt();
                    {
                        Date currentDate = new Date();
                        long daysSinceLastUpdate  = DateUtils.calculateDaysDifference(lastUpdatedAt, currentDate);

                        // Fill missing data with 0 Sales
                        for (int i=0; i<daysSinceLastUpdate;i++){
                            dailySales.add(0.);
                            dailySold.add(0);
                        }

                        // If no transactions yet, do not proceed
                        if (dailySales.isEmpty()){
                            return Tasks.forResult(new SalesAndSoldItemsReportData());
                        }
                    }

                    // Now you have dailySales and dailySold, you can now compute the report data
                    Collections.reverse(dailySales);
                    Collections.reverse(dailySold);
                    SalesAndSoldItemsReportData result = new SalesAndSoldItemsReportData();

                    // Week Data
                    List<List<Double>> groupBy7DailySales = createSubgroups(dailySales, 7);
                    List<List<Integer>> groupBy7DailySold = createSubgroups(dailySold, 7);

                    result.weekCurrentSales = (float) groupBy7DailySales.get(0).stream().mapToDouble(Double::doubleValue).sum();
                    if (groupBy7DailySales.size()>1) {
                        result.weekPreviousSales = (float) groupBy7DailySales.get(1).stream().mapToDouble(Double::doubleValue).sum();
                    }
                    result.weekCurrentSoldItems = groupBy7DailySold.get(0).stream().mapToInt(Integer::intValue).sum();
                    if (groupBy7DailySold.size()>1) {
                        result.weekPreviousSoldItems = groupBy7DailySold.get(1).stream().mapToInt(Integer::intValue).sum();
                    }

                    // Month Data
                    List<List<Double>> groupBy30DailySales = createSubgroups(dailySales, 30);
                    List<List<Integer>> groupBy30DailySold = createSubgroups(dailySold, 30);

                    result.monthCurrentSales = (float) groupBy30DailySales.get(0).stream().mapToDouble(Double::doubleValue).sum();
                    if (groupBy30DailySales.size()>1){
                        result.monthPreviousSales = (float) groupBy30DailySales.get(1).stream().mapToDouble(Double::doubleValue).sum();
                    }
                    result.monthCurrentSoldItems = groupBy30DailySold.get(0).stream().mapToInt(Integer::intValue).sum();
                    if (groupBy30DailySold.size()>1){
                        result.monthPreviousSoldItems = groupBy30DailySold.get(1).stream().mapToInt(Integer::intValue).sum();
                    }

                    // Year Data
                    List<List<Double>> groupBy360DailySales = createSubgroups(dailySales, 360);
                    List<List<Integer>> groupBy360DailySold = createSubgroups(dailySold, 360);

                    result.yearCurrentSales = (float) groupBy360DailySales.get(0).stream().mapToDouble(Double::doubleValue).sum();
                    if (groupBy360DailySales.size()>1){
                        result.yearPreviousSales = (float) groupBy360DailySales.get(1).stream().mapToDouble(Double::doubleValue).sum();
                    }
                    result.yearCurrentSoldItems = groupBy360DailySold.get(0).stream().mapToInt(Integer::intValue).sum();
                    if (groupBy360DailySold.size()>1){
                        result.yearPreviousSoldItems = groupBy360DailySold.get(1).stream().mapToInt(Integer::intValue).sum();
                    }
                    return Tasks.forResult(result);
                })
            ;
    }

    public static <T> List<List<T>> createSubgroups(List<T> originalList, int n) {
        List<List<T>> subgroups = new ArrayList<>();

        // Iterate over the original list and create subgroups
        for (int startIndex = 0; startIndex < originalList.size(); startIndex += n) {
            // Calculate the end index for the current subgroup
            int endIndex = Math.min(startIndex + n, originalList.size());
            // Create a sublist from startIndex to endIndex
            List<T> subgroup = originalList.subList(startIndex, endIndex);
            // Add the subgroup to the list of subgroups
            subgroups.add(subgroup);
        }

        return subgroups;
    }

}
