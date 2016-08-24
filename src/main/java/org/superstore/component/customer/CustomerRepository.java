package org.superstore.component.customer;

import org.springframework.data.repository.Repository;
import org.superstore.model.customer.Customer;

/**
 * @author Roman Szarowski
 */
public interface CustomerRepository extends Repository<Customer, String> {

    Customer save(Customer customer);

    Customer findByName(String name);
}