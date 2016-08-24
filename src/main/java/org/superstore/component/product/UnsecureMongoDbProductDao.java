package org.superstore.component.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.superstore.exception.ProductNotFoundException;
import org.superstore.model.product.Product;
import org.superstore.model.product.ProductBuilder;
import org.superstore.model.product.ProductDto;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * This DAO service class handles unsecure access of {@link Product} objects to MongoDB database.
 *
 * @author Roman Szarowski
 */
@Service(value = "UnsecureMongoDbProductDao")
public class UnsecureMongoDbProductDao implements ProductDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsecureMongoDbProductDao.class);

    @Autowired
    protected ProductRepository repository;

    @Override
    public ProductDto create(ProductDto product) {
        LOGGER.info("Creating a new product entry with information: {}", product);

        Product persisted = new ProductBuilder()
                .name(product.getName())
                .description(product.getDescription())
                .prices(product.getPrices())
                .build();

        persisted = repository.save(persisted);
        LOGGER.info("Created a new product entry with information: {}", persisted);

        return convertToDto(persisted);
    }

    @Override
    public ProductDto delete(String id) {
        LOGGER.info("Deleting a product entry with id: {}", id);

        Product deleted = findProductById(id);
        repository.delete(deleted);

        LOGGER.info("Deleted product entry with informtation: {}", deleted);

        return convertToDto(deleted);
    }

    @Override
    public List<ProductDto> findAll() {
        LOGGER.info("Finding all product entries.");

        List<Product> productEntries = repository.findAll();

        LOGGER.info("Found {} product entries", productEntries.size());

        return convertToDtos(productEntries);
    }

    List<ProductDto> convertToDtos(List<Product> models) {
        return models.stream()
                .map(this::convertToDto)
                .collect(toList());
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

        Product updated = findProductById(product.getId());
        updated.update(product.getName(), product.getDescription(), product.getPrices());
        updated = repository.save(updated);

        LOGGER.info("Updated product entry with information: {}", updated);

        return convertToDto(updated);
    }

    Product findProductById(String id) {
        Optional<Product> result = repository.findOne(id);
        return result.orElseThrow(() -> new ProductNotFoundException(id));

    }

    ProductDto convertToDto(Product model) {
        ProductDto dto = new ProductDto();

        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setPrices(model.getPrices());

        return dto;
    }
}