package org.superstore.model;

/**
 * @author Roman Szarowski
 */
final class FieldErrorDto {

    private final String field;

    private final String message;

    FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getField() {
        return field;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }
}
