package com.projectfkklp.saristorepos.classes;

public class ValidationException extends Exception{
    private ValidationStatus validationStatus;

    public ValidationStatus getValidationStatus(){
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus){
        this.validationStatus = validationStatus;
    }

    public ValidationException(ValidationStatus validationStatus){
        super();
        this.validationStatus = validationStatus;
    }
}
