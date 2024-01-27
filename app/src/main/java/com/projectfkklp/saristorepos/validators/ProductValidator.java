package com.projectfkklp.saristorepos.validators;

import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.utils.StringUtils;

public class ProductValidator {
    public static ValidationStatus validate(Product product) {
        ValidationStatus validationStatus = new ValidationStatus();

        if (StringUtils.isNullOrEmpty(product.getName())) {
            validationStatus.putError("name", "Product Name is required");
        }

        return validationStatus;
    }
}
