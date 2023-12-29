package com.projectfkklp.saristorepos.enums;

public enum UserRole {
    OWNER(1, "Owner"),
    ASSISTANT(2, "Assistant");

    public final int value;
    public final String label;

    UserRole(int value, String label) {
        this.value = value;
        this.label = label;
    }
}
