package org.superstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * This application class has three responsibilities:
 * <ol>
 * <li>It enables the auto configuration of the Spring application context.</li>
 * <li>
 * It ensures that Spring looks for other components (controllers, services, and repositories) from the
 * <code>org.superstore.product</code> package.
 * </li>
 * <li>It launches our application in the main() method.</li>
 * </ol>
 *
 * @author Roman Szarowski
 */
@SpringBootApplication
public class ProductApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}