package org.superstore.component;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.superstore.component.product.MongoDbProductDao;
import org.superstore.exception.ProductNotFoundException;

/**
 * @author Roman Szarowski
 */
@RunWith(MockitoJUnitRunner.class)
public class MongoDbProductDaoTest extends UnsecureMongoDbProductDaoTest {

    @InjectMocks
    protected MongoDbProductDao service;

    @Before
    public void setUp() {
    }

    @Test
    public void create_ShouldSaveNewProductEntry() {
        super.create_ShouldSaveNewProductEntry();
    }

    @Test
    public void create_ShouldReturnTheInformationOfCreatedProductEntry() {
        super.create_ShouldReturnTheInformationOfCreatedProductEntry();
    }

    @Test(expected = ProductNotFoundException.class)
    public void delete_ProductEntryNotFound_ShouldThrowException() {
        super.delete_ProductEntryNotFound_ShouldThrowException();
    }

    @Test
    public void delete_ProductEntryFound_ShouldDeleteTheFoundProductEntry() {
        super.delete_ProductEntryFound_ShouldDeleteTheFoundProductEntry();
    }

    @Test
    public void delete_ProductEntryFound_ShouldReturnTheDeletedProductEntry() {
        super.delete_ProductEntryFound_ShouldReturnTheDeletedProductEntry();
    }

    @Test
    public void findAll_OneProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry() {
        super.findAll_OneProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry();
    }

    @Test(expected = ProductNotFoundException.class)
    public void findById_ProductEntryNotFound_ShouldThrowException() {
        super.findById_ProductEntryNotFound_ShouldThrowException();
    }

    @Test
    public void findById_ProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry() {
        super.findById_ProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry();
    }

    @Test(expected = ProductNotFoundException.class)
    public void update_UpdatedProductEntryNotFound_ShouldThrowException() {
        super.update_UpdatedProductEntryNotFound_ShouldThrowException();
    }

    @Test
    public void update_UpdatedProductEntryFound_ShouldSaveUpdatedProductEntry() {
        super.update_UpdatedProductEntryFound_ShouldSaveUpdatedProductEntry();
    }

    @Test
    public void update_UpdatedProductEntryFound_ShouldReturnTheInformationOfUpdatedProductEntry() {
        super.update_UpdatedProductEntryFound_ShouldReturnTheInformationOfUpdatedProductEntry();
    }
}