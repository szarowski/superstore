package org.superstore.model.product;

import org.superstore.validation.Validator;

import java.util.Map;

/**
 * We don't have to use the builder pattern here because the constructed class has only several fields.
 * However, I use the builder pattern in this example because it makes the code a bit easier to read.
 */
public class ProductBuilder {

    String name;

    String description;

    Map<String, Double> prices;

    public ProductBuilder() {
    }

    public ProductBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder prices(Map<String, Double> prices) {
        this.prices = prices;
        return this;
    }

    public Product build() {
        Product build = new Product(this);
        Validator.checkNameDescriptionAndPrices(build.getName(), build.getDescription(), build.getPrices());
        return build;
    }
}