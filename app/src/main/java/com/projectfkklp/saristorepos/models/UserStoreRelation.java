package com.projectfkklp.saristorepos.models;

import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;

public class UserStoreRelation {
    private String id;
    private String userId;
    private String storeId;
    private UserRole role;
    private UserStatus status;

    // Empty constructor
    public UserStoreRelation() {
        // Default constructor with no parameters
    }

    // Constructor to initialize all fields
    public UserStoreRelation(String id, String userId, String storeId, UserRole role, UserStatus status) {
        this.id = id;
        this.userId = userId;
        this.storeId = storeId;
        this.role = role;
        this.status = status;
    }

    // Getter and Setter methods for 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter methods for 'userId'
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter methods for 'storeId'
    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    // Getter and Setter methods for 'role'
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // Getter and Setter methods for 'status'
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
