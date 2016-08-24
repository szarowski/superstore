package org.superstore.component.product;

import org.springframework.data.repository.Repository;
import org.superstore.model.product.Product;

import java.util.List;
import java.util.Optional;

/**
 * This repository provides CRUD operations for {@link Product}
 * objects.
 * @author Roman Szarowski
 */
public interface ProductRepository extends Repository<Product, String> {

    /**
     * Deletes a product entry from the database.
     * @param deleted   The deleted product entry.
     */
    void delete(Product deleted);

    /**
     * Finds all product entries from the database.
     * @return  The information of all product entries that are found from the database.
     */
    List<Product> findAll();

    /**
     * Finds the information of a single product entry.
     * @param id    The id of the requested product entry.
     * @return      The information of the found product entry. If no product entry
     *              is found, this method returns an empty {@link java.util.Optional} object.
     */
    Optional<Product> findOne(String id);

    /**
     * Saves a new product entry to the database.
     * @param saved The information of the saved product entry.
     * @return      The information of the saved product entry.
     */
    Product save(Product saved);
}
