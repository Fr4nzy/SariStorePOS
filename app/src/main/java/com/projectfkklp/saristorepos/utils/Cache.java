package com.projectfkklp.saristorepos.utils;

import android.content.SharedPreferences;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Cache {
    public static final String PREFERENCES_NAME = "com.projectfkklp.saristorepos";

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public static <T> void saveObject(Context context, String key, T obj) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, Serializer.serialize(obj));
        editor.apply();
    }

    public static <T> T getObject(Context context, String key, Class<T> type) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        String serializedObject = prefs.getString(key, null);
        return Serializer.deserialize(serializedObject,type);
    }

    public static <T> void saveObjectList(Context context, String key, List<T> objList) {
        int size = objList.size();
        for (int i=0; i<size;i++) {
            T obj = objList.get(i);
            saveObject(context, key+"_"+i, obj);
        }
        putInt(context, key+"_size",  size);
    }

    public static <T> List<T> getObjectList(Context context, String key, Class<T> type) {
        int size = getInt(context, key+"_size");
        List<T> objList = new ArrayList<>();
        for (int i=0; i<size;i++) {
            objList.add(getObject(context, key+"_"+i, type));
        }
        return objList;
    }

    public static void dump(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
