package com.projectfkklp.saristorepos.validators;

import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.User;

public class UserRegistrationValidator {
    public static ValidationStatus validate(User user) {
        ValidationStatus validationStatus = new ValidationStatus();

        if (user.getName().isEmpty()) {
            validationStatus.putError("name", "Name is required");
        }

        return validationStatus;
    }
}
