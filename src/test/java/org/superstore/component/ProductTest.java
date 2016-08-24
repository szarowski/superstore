package org.superstore.component;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.superstore.model.product.ProductAssert;
import org.superstore.model.product.Product;
import org.superstore.model.product.ProductBuilder;
import org.superstore.util.StringTestUtil;

import java.util.HashMap;
import java.util.Map;

import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_GBP;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_USD;

/**
 * @author Roman Szarowski
 */
public class ProductTest {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final Map<String, Double> PRICES = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 123D);
            put(REQUIRED_PRICE_GBP, 100D);
        }
    };

    private static final int MAX_LENGTH_DESCRIPTION = 500;
    private static final int MAX_LENGTH_NAME = 100;

    private static final String UPDATED_DESCRIPTION = "updatedDescription";
    private static final String UPDATED_NAME = "updatedName";

    private static final Map<String, Double> UPDATED_PRICES = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 321D);
            put(REQUIRED_PRICE_GBP, 300D);
        }
    };

    @Test(expected = NullPointerException.class)
    public void build_NameIsNull_ShouldThrowException() {
        new ProductBuilder()
                .name(null)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_NameIsEmpty_ShouldThrowException() {
        new ProductBuilder()
                .name("" )
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void build_PricesIsNull_ShouldThrowException() {
        new ProductBuilder()
                .name(null)
                .description(DESCRIPTION)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_PricesIsEmpty_ShouldThrowException() {
        new ProductBuilder()
                .name("" )
                .description(DESCRIPTION)
                .prices(Maps.newHashMap())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_NameIsTooLong_ShouldThrowException() {
        String tooLongName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME + 1);
        new ProductBuilder()
                .name(tooLongName)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_DescriptionIsTooLong_ShouldThrowException() {
        String tooLongDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION + 1);
        new ProductBuilder()
                .name(NAME)
                .description(tooLongDescription)
                .prices(PRICES)
                .build();
    }

    @Test
    public void build_WithoutDescription_ShouldCreateNewProductEntryWithCorrectName() {
        Product build = new ProductBuilder()
                .name(NAME)
                .prices(PRICES)
                .build();

        ProductAssert.assertThatProduct(build)
                .hasNoId()
                .hasName(NAME)
                .hasNoDescription()
                .hasPrices(PRICES);
    }

    @Test
    public void build_WithNameAndDescriptionAndPrices_ShouldCreateNewProductEntryWithCorrectFields() {
        Product build = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        ProductAssert.assertThatProduct(build)
                .hasNoId()
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test
    public void build_WithMaxLengthNameAndDescriptionAndPrices_ShouldCreateNewProductEntryWithCorrectFields() {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        Product build = new ProductBuilder()
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        ProductAssert.assertThatProduct(build)
                .hasNoId()
                .hasName(maxLengthName)
                .hasDescription(maxLengthDescription)
                .hasPrices(PRICES);
    }

    @Test(expected = NullPointerException.class)
    public void update_NameIsNull_ShouldThrowException() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        updated.update(null, UPDATED_DESCRIPTION, PRICES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_NameIsEmpty_ShouldThrowException() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        updated.update("", UPDATED_DESCRIPTION, PRICES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_NameIsTooLong_ShouldThrowException() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        String tooLongName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME + 1);
        updated.update(tooLongName, UPDATED_DESCRIPTION, PRICES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_DescriptionIsTooLong_ShouldThrowException() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        String tooLongDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION + 1);
        updated.update(UPDATED_NAME, tooLongDescription, PRICES);
    }

    @Test
    public void update_DescriptionIsNull_ShouldUpdateNameAndDescription() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        updated.update(UPDATED_NAME, null, PRICES);

        ProductAssert.assertThatProduct(updated)
                .hasName(UPDATED_NAME)
                .hasNoDescription();
    }

    @Test
    public void update_MaxLengthNameAndDescription_ShouldUpdateNameAndDescription() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        updated.update(maxLengthName, maxLengthDescription, PRICES);

        ProductAssert.assertThatProduct(updated)
                .hasName(maxLengthName)
                .hasDescription(maxLengthDescription);
    }

    @Test
    public void update_MaxLengthNameAndDescription_ShouldUpdateNameAndDescriptionAndPrices() {
        Product updated = new ProductBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        updated.update(maxLengthName, maxLengthDescription, UPDATED_PRICES);

        ProductAssert.assertThatProduct(updated)
                .hasName(maxLengthName)
                .hasDescription(maxLengthDescription)
                .hasPrices(UPDATED_PRICES);
    }
}
