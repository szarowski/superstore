package org.superstore.model.product;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_GBP;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_USD;

/**
 * @author Roman Szarowski
 */
public class TestProductBuilder {

    private String id;
    private String name = "NOT_IMPORTANT";
    private String description;
    private Map<String, Double> prices = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 0D);
            put(REQUIRED_PRICE_GBP, 0D);
        }
    };

    public TestProductBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TestProductBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TestProductBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TestProductBuilder prices(Map<String, Double> prices) {
        this.prices = prices;
        return this;
    }

    public Product build() {
        Product product = new ProductBuilder()
                .name(name)
                .description(description)
                .prices(prices)
                .build();

        ReflectionTestUtils.setField(product, "id", id);

        return product;
    }
}
