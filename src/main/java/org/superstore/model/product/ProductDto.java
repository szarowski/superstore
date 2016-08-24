package org.superstore.model.product;

import org.hibernate.validator.constraints.NotEmpty;
import org.superstore.validation.ValidCurrency;

import javax.validation.constraints.Size;
import java.util.Map;
import java.util.stream.Collectors;

import static org.superstore.validation.Validator.MAX_LENGTH_DESCRIPTION;
import static org.superstore.validation.Validator.MAX_LENGTH_NAME;

/**
 * This data transfer object contains the information of a single product
 * entry and specifies validation rules that are used to ensure that only
 * valid information can be saved to the used database.
 *
 * @author Roman Szarowski
 */
public final class ProductDto {

    private String id;

    @Size(max = MAX_LENGTH_DESCRIPTION)
    private String description;

    @NotEmpty
    @Size(max = MAX_LENGTH_NAME)
    private String name;

    @ValidCurrency
    private Map<String, Double> prices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Double> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, Double> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return String.format(
                "ProductDto[id=%s, description=%s, name=%s, Prices=[%s]]",
                this.id,
                this.name,
                this.description,
                this.prices.entrySet().stream().map(entry -> entry.getKey() + " : " + entry.getValue())
                        .collect(Collectors.joining(", " )));
    }
}
