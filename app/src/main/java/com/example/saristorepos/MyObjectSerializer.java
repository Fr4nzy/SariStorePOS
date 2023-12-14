package com.example.saristorepos;

import com.google.gson.Gson;
import com.workday.insights.timeseries.arima.struct.ArimaModel;

public class MyObjectSerializer {
    private static final Gson gson = new Gson();

    // Serialize the object to a JSON string
    public static String serialize(ArimaModel myObject) {
        return gson.toJson(myObject);
    }

    // Deserialize the JSON string to an object
    public static ArimaModel deserialize(String jsonString) {
        return gson.fromJson(jsonString, ArimaModel.class);
    }
}
