package org.superstore.component.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.superstore.exception.ProductNotFoundException;
import org.superstore.model.product.ProductDto;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * This controller provides the public API that is used to manage the information
 * of product entries authenticated using Spring Web Authentication.
 *
 * @author Roman Szarowski
 */
@RestController
@RequestMapping("/store/products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Resource(name = "MongoDbProductDao")
    private ProductDao service;
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    ProductDto create(@RequestBody @Valid ProductDto productEntry) {
        LOGGER.info("Creating a new product entry with information: {}", productEntry);

        ProductDto created = service.create(productEntry);
        LOGGER.info("Created a new product entry with information: {}", created);

        return created;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    ProductDto delete(@PathVariable("id") String id) {
        LOGGER.info("Deleting product entry with id: {}", id);

        ProductDto deleted = service.delete(id);
        LOGGER.info("Deleted product entry with information: {}", deleted);

        return deleted;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<ProductDto> findAll() {
        LOGGER.info("Finding all product entries");

        List<ProductDto> productEntries = service.findAll();
        LOGGER.info("Found {} product entries", productEntries.size());

        return productEntries;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    ProductDto findById(@PathVariable("id") String id) {
        LOGGER.info("Finding product entry with id: {}", id);

        ProductDto productEntry = service.findById(id);
        LOGGER.info("Found product entry with information: {}", productEntry);

        return productEntry;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    ProductDto update(@RequestBody @Valid ProductDto productEntry) {
        LOGGER.info("Updating product entry with information: {}", productEntry);

        ProductDto updated = service.update(productEntry);
        LOGGER.info("Updated product entry with information: {}", updated);

        return updated;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleProductNotFound(ProductNotFoundException ex) {
        LOGGER.error("Handling exception with message: {}", ex.getMessage());
    }

    // JUnit purposes only
    void setService(ProductDao service) {
        this.service = service;
    }
}