package org.superstore.model.product;

import org.assertj.core.util.Maps;

import java.util.Map;

/**
 * @author Roman Szarowski
 */
public class ProductDtoBuilder {

    private String description;
    private String id;
    private String name;
    private Map<String, Double> prices = Maps.newHashMap();

    public ProductDtoBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProductDtoBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductDtoBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductDtoBuilder prices(Map<String, Double> prices) {
        this.prices = prices;
        return this;
    }

    public ProductDto build() {
        ProductDto dto = new ProductDto();

        dto.setDescription(description);
        dto.setId(id);
        dto.setName(name);
        dto.setPrices(prices);

        return dto;
    }
}
