package org.superstore.model.product;

import org.assertj.core.api.AbstractAssert;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roman Szarowski
 */
public class ProductDtoAssert extends AbstractAssert<ProductDtoAssert, ProductDto> {

    private ProductDtoAssert(ProductDto actual) {
        super(actual, ProductDtoAssert.class);
    }

    public static ProductDtoAssert assertThatProductDto(ProductDto actual) {
        return new ProductDtoAssert(actual);
    }

    public ProductDtoAssert hasId(String expectedId) {
        isNotNull();

        String actualId = actual.getId();
        assertThat(actualId)
                .overridingErrorMessage("Expected id to be <%s> but was <%s>",
                        expectedId,
                        actualId
                )
                .isEqualTo(expectedId);

        return this;
    }

    public ProductDtoAssert hasNoId() {
        isNotNull();

        String actualId = actual.getId();
        assertThat(actualId)
                .overridingErrorMessage("Expected id to be <null> but was <%s>", actualId)
                .isNull();

        return this;
    }

    public ProductDtoAssert hasName(String expectedName) {
        isNotNull();

        String actualName = actual.getName();
        assertThat(actualName)
                .overridingErrorMessage("Expected name to be <%s> but was <%s>",
                        expectedName,
                        actualName
                )
                .isEqualTo(expectedName);

        return this;
    }

    public ProductDtoAssert hasDescription(String expectedDescription) {
        isNotNull();

        String actualDescription = actual.getDescription();
        assertThat(actualDescription)
                .overridingErrorMessage("Expected description to be <%s> but was <%s>",
                        expectedDescription,
                        actualDescription
                )
                .isEqualTo(expectedDescription);

        return this;
    }

    public ProductDtoAssert hasNoDescription() {
        isNotNull();

        String actualDescription = actual.getDescription();
        assertThat(actualDescription)
                .overridingErrorMessage("expected description to be <null> but was <%s>", actualDescription)
                .isNull();

        return this;
    }

    public ProductDtoAssert hasPrices(Map<String, Double> expectedPrices) {
        isNotNull();

        Map<String, Double> actualPrices = actual.getPrices();
        assertThat(actualPrices)
                .overridingErrorMessage("Expected prices must contain prices in USD and GBP <%s> but was <%s>",
                        expectedPrices.entrySet().stream()
                                .map(entry -> entry.getKey() + " : " + entry.getValue())
                                .collect(Collectors.joining(", " )),
                        actualPrices.entrySet().stream()
                                .map(entry -> entry.getKey() + " : " + entry.getValue())
                                .collect(Collectors.joining(", " )))
                .isEqualTo(expectedPrices);

        return this;
    }
}