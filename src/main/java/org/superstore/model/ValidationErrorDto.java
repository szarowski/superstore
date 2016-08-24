package org.superstore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ValidationErrorDto {

    private final List<FieldErrorDto> fieldErrors = new ArrayList<>();

    public void addFieldError(String path, String message) {
        FieldErrorDto error = new FieldErrorDto(path, message);
        fieldErrors.add(error);
    }

    public List<FieldErrorDto> getFieldErrors() {
        return Collections.unmodifiableList(fieldErrors);
    }
}
