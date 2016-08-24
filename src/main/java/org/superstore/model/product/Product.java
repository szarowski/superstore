package org.superstore.model.product;

import org.springframework.data.annotation.Id;
import org.superstore.validation.Validator;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Roman Szarowski
 */
public final class Product {

    @Id
    @SuppressWarnings("unused" )
    private String id;

    private String description;

    private String name;

    private Map<String, Double> prices;

    public Product() {
    }

    Product(ProductBuilder productBuilder) {
        this.description = productBuilder.description;
        this.name = productBuilder.name;
        this.prices = productBuilder.prices;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Double> getPrices() {
        return prices;
    }

    public void update(String name, String description, Map<String, Double> prices) {
        Validator.checkNameDescriptionAndPrices(name, description, prices);

        this.name = name;
        this.description = description;
        this.prices = prices;
    }

    @Override
    public String toString() {
        return String.format(
                "ProductDto[id=%s, description=%s, name=%s, Prices=[%s]]",
                this.id,
                this.description,
                this.name,
                this.prices.entrySet().stream().map(entry -> entry.getKey() + " : " + entry.getValue())
                        .collect(Collectors.joining(", " )));
    }
}
