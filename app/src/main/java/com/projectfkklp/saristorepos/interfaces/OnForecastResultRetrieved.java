package com.projectfkklp.saristorepos.interfaces;

import com.workday.insights.timeseries.arima.struct.ForecastResult;

public interface OnForecastResultRetrieved {
    void onForecastResultRetrieved(ForecastResult result);
}
