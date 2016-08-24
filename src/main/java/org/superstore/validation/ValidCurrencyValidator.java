package org.superstore.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is an annotation implementation for the currency validation.
 *
 * @author Roman Szarowski
 */
public class ValidCurrencyValidator implements ConstraintValidator<ValidCurrency, Map<String, Double>> {

    public static final String REQUIRED_PRICE_USD = "USD";
    public static final String REQUIRED_PRICE_GBP = "GBP";

    @Override
    public void initialize(ValidCurrency constraintAnnotation) {
    }

    @Override
    public boolean isValid(Map<String, Double> prices, ConstraintValidatorContext context) {
        return prices != null && !prices.isEmpty()
                && prices.containsKey(REQUIRED_PRICE_USD) && prices.containsKey(REQUIRED_PRICE_GBP)
                && prices.values() != null
                && prices.values().stream().filter(value -> value == null || value < 0 || value > Double.MAX_VALUE)
                .collect(Collectors.toList()).isEmpty();
    }
}