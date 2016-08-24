package org.superstore.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.superstore.component.customer.CustomerRepository;
import org.superstore.model.customer.Customer;
import org.superstore.model.product.Product;

/**
 * @author Roman Szarowski
 */
@Component
@RepositoryEventHandler(Product.class)
@SuppressWarnings("unused")
public class SpringDataRestEventHandler {

    private final CustomerRepository customerRepository;

    @Autowired
    public SpringDataRestEventHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @HandleBeforeCreate
    public void applyUserInformationUsingSecurityContext(Product product) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = this.customerRepository.findByName(name);
        if (customer == null) {
            Customer newCustomer = new Customer();
            newCustomer.setName(name);
            newCustomer.setRoles(new String[]{"ROLE_CUSTOMER"});
        }
    }
}