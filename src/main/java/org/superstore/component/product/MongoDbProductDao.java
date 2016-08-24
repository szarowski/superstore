package org.superstore.component.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.superstore.model.product.Product;
import org.superstore.model.product.ProductDto;

import java.util.List;

/**
 * This DAO service class handles access of {@link Product} objects to MongoDB database.
 * The user must be authenticated and must have the ROLE_USER access role assigned.
 *
 * @author Roman Szarowski
 */
@Service(value = "MongoDbProductDao")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class MongoDbProductDao extends UnsecureMongoDbProductDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbProductDao.class);

    @Override
    public ProductDto create(ProductDto product) {
        LOGGER.info("Creating a new product entry with information: {}", product);

        ProductDto persisted = super.create(product);

        LOGGER.info("Created a new product entry with information: {}", persisted);

        return persisted;
    }

    @Override
    public ProductDto delete(String id) {
        LOGGER.info("Deleting a product entry with id: {}", id);

        ProductDto deleted = super.delete(id);

        LOGGER.info("Deleted product entry with informtation: {}", deleted);

        return deleted;
    }

    @Override
    public List<ProductDto> findAll() {
        LOGGER.info("Finding all product entries.");

        List<Product> productEntries = repository.findAll();

        LOGGER.info("Found {} product entries", productEntries.size());

        return convertToDtos(productEntries);
    }

    @Override
    public ProductDto findById(String id) {
        LOGGER.info("Finding product entry with id: {}", id);

        Product found = findProductById(id);

        LOGGER.info("Found product entry: {}", found);

        return convertToDto(found);
    }

    @Override
    public ProductDto update(ProductDto product) {
        LOGGER.info("Updating product entry with information: {}", product);

        ProductDto updated = super.update(product);

        LOGGER.info("Updated product entry with information: {}", updated);

        return updated;
    }
}
