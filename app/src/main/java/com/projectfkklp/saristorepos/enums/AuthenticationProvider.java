package com.projectfkklp.saristorepos.enums;

public enum AuthenticationProvider {
    PHONE(1, "phoneUid"),
    GMAIL(2, "gmailUid");

    public final int value;
    public final String key;

    AuthenticationProvider(int value, String key) {
        this.value = value;
        this.key = key;
    }
}
