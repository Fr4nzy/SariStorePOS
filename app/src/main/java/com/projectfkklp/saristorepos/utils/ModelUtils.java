package com.projectfkklp.saristorepos.utils;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModelUtils {
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    public static long createUniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while(true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime+1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    public static String createShortId(int idLength) {
        Random random = new Random();
        StringBuilder storeId = new StringBuilder();

        for (int i = 0; i < idLength; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            storeId.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }

        return storeId.toString();
    }

    public static String createShortId(){
        long timeId = createUniqueCurrentTimeMS();
        String shortId = createShortId(3);

        return Long.toHexString(timeId) + shortId;
    }

}
