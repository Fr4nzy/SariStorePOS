package com.projectfkklp.saristorepos.utils;

import com.google.gson.Gson;

public class Serializer {
    private static final Gson gson = new Gson();

    // Serialize the object to a JSON string
    public static <T> String serialize(T obj) {
        return gson.toJson(obj);
    }

    // Deserialize the JSON string to an object
    public static <T> T deserialize(String jsonString, Class<T> type) {
        return gson.fromJson(jsonString, type);
    }
}
