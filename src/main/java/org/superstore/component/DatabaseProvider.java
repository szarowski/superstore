package org.superstore.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.superstore.component.customer.CustomerRepository;
import org.superstore.component.product.ProductRepository;
import org.superstore.model.customer.Customer;

/**
 * @author Roman Szarowski
 */
@Component
@SuppressWarnings("unused")
public class DatabaseProvider implements CommandLineRunner {

    private final ProductRepository products;
    private final CustomerRepository customers;

    @Autowired
    public DatabaseProvider(ProductRepository productRepository,
                            CustomerRepository customerRepository) {

        this.products = productRepository;
        this.customers = customerRepository;
    }

    @Override
    public void run(String... strings) throws Exception {

        this.customers.save(new Customer("roman", "szarowski",
                "ROLE_CUSTOMER" ));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("roman", "doesn't matter",
                        AuthorityUtils.createAuthorityList("ROLE_CUSTOMER" )));

        SecurityContextHolder.clearContext();
    }
}