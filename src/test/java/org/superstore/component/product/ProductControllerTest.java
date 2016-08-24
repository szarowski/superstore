package org.superstore.component.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.superstore.component.RestErrorHandler;
import org.superstore.exception.ProductNotFoundException;
import org.superstore.model.product.ProductDto;
import org.superstore.model.product.ProductDtoAssert;
import org.superstore.model.product.ProductDtoBuilder;
import org.superstore.util.StringTestUtil;
import org.superstore.util.WebTestUtil;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_GBP;
import static org.superstore.validation.ValidCurrencyValidator.REQUIRED_PRICE_USD;

/**
 * @author Roman Szarowski
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    private static final String DESCRIPTION = "description";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final Map<String, Double> PRICES = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 123D);
            put(REQUIRED_PRICE_GBP, 100D);
        }
    };
    private static final Map<String, Double> UPDATED_PRICES = new HashMap<String, Double>() {
        {
            put(REQUIRED_PRICE_USD, 321D);
            put(REQUIRED_PRICE_GBP, 300D);
        }
    };


    private static final int MAX_LENGTH_DESCRIPTION = 500;
    private static final int MAX_LENGTH_NAME = 100;

    @Mock
    private MongoDbProductDao service;

    @InjectMocks
    private ProductController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        controller.setService(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setHandlerExceptionResolvers(withExceptionControllerAdvice())
                .build();
    }

    /**
     * For some reason this does not work. The correct exception handler method is invoked but when it tries
     * to return the validation errors as json, the following exception appears to log:
     * <p>
     * Failed to invoke @ExceptionHandler method:
     * public ValidationErrorDto RestErrorHandler.processValidationError(org.springframework.web.bind.MethodArgumentNotValidException)
     * org.springframework.web.HttpMediaTypeNotAcceptableException: Could not find acceptable representation
     * <p>
     * I have to figure out how to fix this before I can write the unit tests that ensure that validation is working.
     */
    ExceptionHandlerExceptionResolver withExceptionControllerAdvice() {
        final ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(final HandlerMethod handlerMethod,
                                                                              final Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(RestErrorHandler.class).resolveMethod(exception);
                if (method != null) {
                    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
                    messageSource.setBasename("messages");
                    return new ServletInvocableHandlerMethod(new RestErrorHandler(messageSource), method);
                }
                return super.getExceptionHandlerMethod(handlerMethod, exception);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    @Test
    public void create_ProductEntryWithOnlyName_ShouldCreateNewProductEntryWithoutDescription() throws Exception {
        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(NAME)
                .prices(PRICES)
                .build();

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        );

        ArgumentCaptor<ProductDto> createdArgument = ArgumentCaptor.forClass(ProductDto.class);
        verify(service, times(1)).create(createdArgument.capture());
        verifyNoMoreInteractions(service);

        ProductDto created = createdArgument.getValue();
        ProductDtoAssert.assertThatProductDto(created)
                .hasNoId()
                .hasName(NAME)
                .hasNoDescription()
                .hasPrices(PRICES);
    }

    @Test
    public void create_ProductEntryWithOnlyName_ShouldReturnResponseStatusCreated() throws Exception {
        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(NAME)
                .prices(PRICES)
                .build();

        when(service.create(isA(ProductDto.class))).then(invocationOnMock -> {
            ProductDto saved = (ProductDto) invocationOnMock.getArguments()[0];
            saved.setId(ID);
            return saved;
        });

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void create_ProductEntryWithOnlyName_ShouldReturnTheInformationOfCreatedProductEntryAsJSon() throws Exception {
        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(NAME)
                .prices(PRICES)
                .build();

        when(service.create(isA(ProductDto.class))).then(invocationOnMock -> {
            ProductDto saved = (ProductDto) invocationOnMock.getArguments()[0];
            saved.setId(ID);
            return saved;
        });

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", isEmptyOrNullString()))
                .andExpect(jsonPath("$.prices", is(PRICES)));
    }

    @Test
    public void create_ProductEntryWithMaxLengthNameAndDescription_ShouldCreateNewProductEntryWithCorrectInformation() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        );

        ArgumentCaptor<ProductDto> createdArgument = ArgumentCaptor.forClass(ProductDto.class);
        verify(service, times(1)).create(createdArgument.capture());
        verifyNoMoreInteractions(service);

        ProductDto created = createdArgument.getValue();
        ProductDtoAssert.assertThatProductDto(created)
                .hasNoId()
                .hasName(maxLengthName)
                .hasDescription(maxLengthDescription)
                .hasPrices(PRICES);
    }

    @Test
    public void create_ProductEntryWithMaxLengthNameAndDescription_ShouldReturnResponseStatusCreated() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        when(service.create(isA(ProductDto.class))).then(invocationOnMock -> {
            ProductDto saved = (ProductDto) invocationOnMock.getArguments()[0];
            saved.setId(ID);
            return saved;
        });

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void create_ProductEntryWithMaxLengthNameAndDescription_ShouldReturnTheInformationOfCreatedProductEntryAsJson() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto newProductEntry = new ProductDtoBuilder()
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        when(service.create(isA(ProductDto.class))).then(invocationOnMock -> {
            ProductDto saved = (ProductDto) invocationOnMock.getArguments()[0];
            saved.setId(ID);
            return saved;
        });

        mockMvc.perform(post("/store/products")
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(newProductEntry))
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(maxLengthName)))
                .andExpect(jsonPath("$.description", is(maxLengthDescription)))
                .andExpect(jsonPath("$.prices", is(PRICES)));
    }

    @Test
    public void delete_ProductEntryNotFound_ShouldReturnResponseStatusNotFound() throws Exception {
        when(service.delete(ID)).thenThrow(new ProductNotFoundException(ID));

        mockMvc.perform(delete("/store/products/{id}", ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_ProductEntryFound_ShouldReturnResponseStatusOk() throws Exception {
        ProductDto deleted = new ProductDtoBuilder()
                .id(ID)
                .build();

        when(service.delete(ID)).thenReturn(deleted);

        mockMvc.perform(delete("/store/products" + "/{id}", ID))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_ProductEntryFound_ShouldTheInformationOfDeletedProductEntryAsJson() throws Exception {
        ProductDto deleted = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(service.delete(ID)).thenReturn(deleted);

        mockMvc.perform(delete("/store/products" + "/{id}", ID))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.prices", is(PRICES)));
    }

    @Test
    public void findAll_ShouldReturnResponseStatusOk() throws Exception {
        mockMvc.perform(get("/store/products"))
                .andExpect(status().isOk());
    }

    @Test
    public void findAll_OneProductEntryFound_ShouldReturnListThatContainsOneProductEntryAsJson() throws Exception {
        ProductDto found = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(service.findAll()).thenReturn(Collections.singletonList(found));

        mockMvc.perform(get("/store/products"))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(ID)))
                .andExpect(jsonPath("$[0].name", is(NAME)))
                .andExpect(jsonPath("$[0].description", is(DESCRIPTION)))
                .andExpect(jsonPath("$[0].prices", is(PRICES)));
    }

    @Test
    public void findById_ProductEntryFound_ShouldReturnResponseStatusOk() throws Exception {
        ProductDto found = new ProductDtoBuilder().build();

        when(service.findById(ID)).thenReturn(found);

        mockMvc.perform(get("/store/products" + "/{id}", ID))
                .andExpect(status().isOk());
    }

    @Test
    public void findById_ProductEntryFound_ShouldTheInformationOfFoundProductEntryAsJson() throws Exception {
        ProductDto found = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .prices(PRICES)
                .build();

        when(service.findById(ID)).thenReturn(found);

        mockMvc.perform(get("/store/products" + "/{id}", ID))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.prices", is(PRICES)));
    }

    @Test
    public void findById_ProductEntryNotFound_ShouldReturnResponseStatusNotFound() throws Exception {
        when(service.findById(ID)).thenThrow(new ProductNotFoundException(ID));

        mockMvc.perform(get("/store/products" + "/{id}", ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_ProductEntryWithOnlyName_ShouldUpdateTheInformationOfProductEntry() throws Exception {
        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .prices(PRICES)
                .build();

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        );

        ArgumentCaptor<ProductDto> updatedArgument = ArgumentCaptor.forClass(ProductDto.class);
        verify(service, times(1)).update(updatedArgument.capture());
        verifyNoMoreInteractions(service);

        ProductDto updated = updatedArgument.getValue();
        ProductDtoAssert.assertThatProductDto(updated)
                .hasId(ID)
                .hasName(NAME)
                .hasNoDescription()
                .hasPrices(PRICES);
    }

    @Test
    public void update_ProductEntryWithOnlyName_ShouldReturnResponseStatusOk() throws Exception {
        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .prices(PRICES)
                .build();

        when(service.update(isA(ProductDto.class))).then(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void update_ProductEntryWithOnlyName_ShouldReturnTheInformationOfUpdatedProductEntryAsJSon() throws Exception {
        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(NAME)
                .prices(PRICES)
                .build();

        when(service.update(isA(ProductDto.class))).then(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", isEmptyOrNullString()))
                .andExpect(jsonPath("$.prices", is(PRICES)));
    }

    @Test
    public void update_ProductEntryWithMaxLengthNameAndDescription_ShouldUpdateTheInformationOfProductEntry() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        );

        ArgumentCaptor<ProductDto> updatedArgument = ArgumentCaptor.forClass(ProductDto.class);
        verify(service, times(1)).update(updatedArgument.capture());
        verifyNoMoreInteractions(service);

        ProductDto updated = updatedArgument.getValue();
        ProductDtoAssert.assertThatProductDto(updated)
                .hasId(ID)
                .hasName(maxLengthName)
                .hasDescription(maxLengthDescription)
                .hasPrices(PRICES);
    }

    @Test
    public void update_ProductEntryWithMaxLengthNameAndDescription_ShouldReturnResponseStatusOk() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(PRICES)
                .build();

        when(service.create(isA(ProductDto.class))).then(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void update_ProductEntryWithMaxLengthNameAndDescription_ShouldReturnTheInformationOfCreatedUpdatedProductEntryAsJson() throws Exception {
        String maxLengthName = StringTestUtil.createStringWithLength(MAX_LENGTH_NAME);
        String maxLengthDescription = StringTestUtil.createStringWithLength(MAX_LENGTH_DESCRIPTION);

        ProductDto updatedProductEntry = new ProductDtoBuilder()
                .id(ID)
                .name(maxLengthName)
                .description(maxLengthDescription)
                .prices(UPDATED_PRICES)
                .build();

        when(service.update(isA(ProductDto.class))).then(invocationOnMock -> invocationOnMock.getArguments()[0]);

        mockMvc.perform(put("/store/products" + "/{id}", ID)
                .contentType(APPLICATION_JSON_UTF8)
                .content(WebTestUtil.convertObjectToJsonBytes(updatedProductEntry))
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(maxLengthName)))
                .andExpect(jsonPath("$.description", is(maxLengthDescription)))
                .andExpect(jsonPath("$.prices", is(UPDATED_PRICES)));
    }
}