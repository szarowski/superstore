package org.superstore.component.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.superstore.model.customer.Customer;

/**
 * @author Roman Szarowski
 */
@Component
public class MongoDbUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Customer customer = this.repository.findByName(name);
        return new User(customer.getName(), customer.getPassword(),
                AuthorityUtils.createAuthorityList(customer.getRoles()));
    }

}