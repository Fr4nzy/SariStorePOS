package com.projectfkklp.saristorepos.enums;

public enum SIgnInMethod {
    PHONE(1, "phoneUid"),
    GMAIL(2, "gmailUid");

    public final int value;
    public final String uidField;

    SIgnInMethod(int value, String uidField) {
        this.value = value;
        this.uidField = uidField;
    }
}
