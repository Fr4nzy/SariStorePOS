package com.projectfkklp.saristorepos.utils;

import android.os.Handler;

public class TestingUtils {

    public static void delay(int delayTime, Runnable  runnable){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delayTime);
    }

}
