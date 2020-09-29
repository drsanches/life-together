package ru.drsanches;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class MyController {

    @Value("${my.value}")
    String value;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "<h2>" + value + "</h2>";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestBody User user) {
        System.out.println(user);
    }
}