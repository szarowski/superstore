package org.superstore.component.product;

import org.superstore.exception.ProductNotFoundException;
import org.superstore.model.product.Product;
import org.superstore.model.product.ProductDto;

import java.util.List;

/**
 * This DAO interface declares the methods that provides CRUD operations for
 * {@link Product} objects.
 *
 * @author Roman Szarowski
 */
public interface ProductDao {

    /**
     * Creates a new product entry.
     *
     * @param product The information of the created product entry.
     * @return The information of the created product entry.
     */
    ProductDto create(ProductDto product);

    /**
     * Deletes a product entry.
     *
     * @param id The id of the deleted product entry.
     * @return THe information of the deleted product entry.
     * @throws ProductNotFoundException if no product entry is found.
     */
    ProductDto delete(String id);

    /**
     * Finds all product entries.
     *
     * @return The information of all product entries.
     */
    List<ProductDto> findAll();

    /**
     * Finds a single product entry.
     *
     * @param id The id of the requested product entry.
     * @return The information of the requested product entry.
     * @throws ProductNotFoundException if no product entry is found.
     */
    ProductDto findById(String id);

    /**
     * Updates the information of a product entry.
     *
     * @param product The information of the updated product entry.
     * @return The information of the updated product entry.
     * @throws ProductNotFoundException if no product entry is found.
     */
    ProductDto update(ProductDto product);
}
