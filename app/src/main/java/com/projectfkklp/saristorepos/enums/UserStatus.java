package com.projectfkklp.saristorepos.enums;

public enum UserStatus {
    ACTIVE(1, "Active"),
    REQUESTED(2, "Requested"),
    INVITED(3, "Invited");

    public final int value;
    public final String label;

    UserStatus(int value, String label) {
        this.value = value;
        this.label = label;
    }
}
