package com.projectfkklp.saristorepos.activities.store_registration;

public class SessionManager {

    private static String currentStore;

    public static void setCurrentStore(String storeId) {
        currentStore = storeId;
    }

    public static String getCurrentStore() {
        return currentStore;
    }
}
