package org.superstore.component;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Roman Szarowski
 */
@Controller
@SuppressWarnings("unused")
public class HomeController {

    @RequestMapping(value = "/" )
    public String index() {
        return "index";
    }

}