package org.superstore.exception;

/**
 * This exception is thrown when the requested product entry is not found.
 * @author Roman Szarowski
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super(String.format("No product entry found with id: <%s>", id));
    }
}
