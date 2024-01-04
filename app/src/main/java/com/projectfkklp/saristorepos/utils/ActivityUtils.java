package com.projectfkklp.saristorepos.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
    public static void navigateTo(Activity activity, Class<?> destinationClass) {
        Intent intent = new Intent(activity, destinationClass);
        activity.startActivity(intent);
        activity.finish(); // Prevent going back to EntryActivity
    }
}
