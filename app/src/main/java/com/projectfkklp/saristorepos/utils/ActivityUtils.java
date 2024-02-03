package com.projectfkklp.saristorepos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActivityUtils {
    public static void navigateTo(Activity activity, Class<?> destinationClass) {
        Intent intent = new Intent(activity, destinationClass);
        activity.startActivity(intent);
        activity.finish(); // Prevent going back to EntryActivity
    }

    public static void hideKeyboard(View view) {
        Context context = view.getContext();
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
