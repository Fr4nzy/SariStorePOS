package com.projectfkklp.saristorepos.classes;

import java.util.HashMap;

public class ValidationStatus{
    private final HashMap<String, String> errors = new HashMap<>();

    public void putError(String field, String message) {
        errors.put(field, message);
    }

    public HashMap<String, String> getErrors(){
        return errors;
    }

    public boolean isValid(){
        return errors.size() == 0;
    }
}
