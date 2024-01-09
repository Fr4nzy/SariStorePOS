package com.projectfkklp.saristorepos.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ToastUtils {
    public static void show(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showFormattedErrors(Context context, HashMap<String, String> errorMap) {
        StringBuilder errorMessage = new StringBuilder();

        int entryCount = errorMap.size();
        int currentEntry = 0;

        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            errorMessage.append(key).append(": ").append(value);

            // Append newline only if it's not the last entry
            if (++currentEntry < entryCount) {
                errorMessage.append("\n");
            }
        }

        // Display the error message in a Toast
        Toast.makeText(context, errorMessage.toString(), Toast.LENGTH_LONG).show();
    }
}
