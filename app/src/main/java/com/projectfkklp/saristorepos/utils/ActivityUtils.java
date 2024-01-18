package com.projectfkklp.saristorepos.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
    public static void navigateTo(Activity activity, Class<?> destinationClass) {
        Intent intent = new Intent(activity, destinationClass);
        activity.startActivity(intent);
        activity.finish(); // Prevent going back to EntryActivity
    }

    public static void navigateToWithFlags(Activity activity, Class<?> destinationClass) {
        Intent intent = new Intent(activity, destinationClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
