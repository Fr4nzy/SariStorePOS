package com.projectfkklp.saristorepos.validators;

import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.Store;
public class StoreValidator {
    public static ValidationStatus validate(Store store) {
        ValidationStatus validationStatus = new ValidationStatus();

        if (store.getName().isEmpty()) {
            validationStatus.putError("name", "Name is required");
        }

        if (store.getAddress().isEmpty()) {
            validationStatus.putError("address", "Address is required");
        }

        return validationStatus;
    }
}
