package com.projectfkklp.saristorepos.activities.store_registration;

public class UserStoreRelation {

        private String userId;
        private String storeId;

        public UserStoreRelation(String userId, String storeId) {
            this.userId = userId;
            this.storeId = storeId;
        }

        // Getters and setters (You may generate them using your IDE)

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStoreId() {
        return storeId;
    }
}


