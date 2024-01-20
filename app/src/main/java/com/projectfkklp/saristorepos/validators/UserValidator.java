package com.projectfkklp.saristorepos.validators;

import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.utils.StringUtils;

public class UserValidator {
    public static ValidationStatus validate(User user) {
        ValidationStatus validationStatus = new ValidationStatus();

        if (user.getName().isEmpty()) {
            validationStatus.putError("name", "Name is required");
        }

        if (StringUtils.isNullOrEmpty(user.getGmailUid()) && StringUtils.isNullOrEmpty(user.getPhoneUid())) {
            validationStatus.putError("identifier", "Gmail or Phone Number is required");
        }

        return validationStatus;
    }
}
