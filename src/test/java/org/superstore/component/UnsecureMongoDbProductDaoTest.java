package org.superstore.component;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.superstore.component.product.ProductRepository;
import org.superstore.component.product.UnsecureMongoDbProductDao;
import org.superstore.exception.ProductNotFoundException;
import org.superstore.model.product.*;

import java.util.*;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_GBP;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_USD;

/**
 * @author Roman Szarowski
 */
@RunWith(MockitoJUnitRunner.class)
public class UnsecureMongoDbProductDaoTest {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final Map<String, Double> PRICES = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 123D);
            put(REQUIRED_PRICE_GBP, 100D);
        }
    };

    @Mock
    private ProductRepository repository;

    @InjectMocks
    protected UnsecureMongoDbProductDao service;

    @Before
    public void setUp() {
    }

    @Test
    public void create_ShouldSaveNewProductEntry() {
        ProductDto newProduct = new ProductDtoBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(repository.save(isA(Product.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        service.create(newProduct);

        ArgumentCaptor<Product> savedProductArgument = ArgumentCaptor.forClass(Product.class);

        verify(repository, times(1)).save(savedProductArgument.capture());
        verifyNoMoreInteractions(repository);

        Product savedProduct = savedProductArgument.getValue();
        ProductAssert.assertThatProduct(savedProduct)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test
    public void create_ShouldReturnTheInformationOfCreatedProductEntry() {
        ProductDto newProduct = new ProductDtoBuilder()
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(repository.save(isA(Product.class))).thenAnswer(invocation -> {
            Product persisted = (Product) invocation.getArguments()[0];
            ReflectionTestUtils.setField(persisted, "id", ID);
            return persisted;
        });

        ProductDto returned = service.create(newProduct);

        ProductDtoAssert.assertThatProductDto(returned)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test(expected = ProductNotFoundException.class)
    public void delete_ProductEntryNotFound_ShouldThrowException() {
        when(repository.findOne(ID)).thenReturn(Optional.empty());

        service.findById(ID);
    }

    @Test
    public void delete_ProductEntryFound_ShouldDeleteTheFoundProductEntry() {
        Product deleted = new TestProductBuilder()
                .id(ID)
                .build();

        when(repository.findOne(ID)).thenReturn(Optional.of(deleted));

        service.delete(ID);

        verify(repository, times(1)).delete(deleted);
    }

    @Test
    public void delete_ProductEntryFound_ShouldReturnTheDeletedProductEntry() {
        Product deleted = new TestProductBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(repository.findOne(ID)).thenReturn(Optional.of(deleted));

        ProductDto returned = service.delete(ID);

        ProductDtoAssert.assertThatProductDto(returned)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test
    public void findAll_OneProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry() {
        Product expected = new TestProductBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(repository.findAll()).thenReturn(Collections.singletonList(expected));

        List<ProductDto> productEntries = service.findAll();
        Assertions.assertThat(productEntries).hasSize(1);

        ProductDto actual = productEntries.iterator().next();
        ProductDtoAssert.assertThatProductDto(actual)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test(expected = ProductNotFoundException.class)
    public void findById_ProductEntryNotFound_ShouldThrowException() {
        when(repository.findOne(ID)).thenReturn(Optional.empty());

        service.findById(ID);
    }

    @Test
    public void findById_ProductEntryFound_ShouldReturnTheInformationOfFoundProductEntry() {
        Product found = new TestProductBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(repository.findOne(ID)).thenReturn(Optional.of(found));

        ProductDto returned = service.findById(ID);

        ProductDtoAssert.assertThatProductDto(returned)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test(expected = ProductNotFoundException.class)
    public void update_UpdatedProductEntryNotFound_ShouldThrowException() {
        when(repository.findOne(ID)).thenReturn(Optional.empty());

        ProductDto updated = new ProductDtoBuilder()
                .id(ID)
                .build();

        service.update(updated);
    }

    @Test
    public void update_UpdatedProductEntryFound_ShouldSaveUpdatedProductEntry() {
        Product existing = new TestProductBuilder()
                .id(ID)
                .build();

        when(repository.findOne(ID)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ProductDto updated = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        service.update(updated);

        verify(repository, times(1)).save(existing);
        ProductAssert.assertThatProduct(existing)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }

    @Test
    public void update_UpdatedProductEntryFound_ShouldReturnTheInformationOfUpdatedProductEntry() {
        Product existing = new TestProductBuilder()
                .id(ID)
                .build();

        when(repository.findOne(ID)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ProductDto updated = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        ProductDto returned = service.update(updated);
        ProductDtoAssert.assertThatProductDto(returned)
                .hasId(ID)
                .hasName(NAME)
                .hasDescription(DESCRIPTION)
                .hasPrices(PRICES);
    }
}