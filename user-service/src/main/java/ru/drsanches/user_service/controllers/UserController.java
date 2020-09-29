package ru.drsanches.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.user_service.data.User;
import ru.drsanches.user_service.data.UserAuth;
import ru.drsanches.user_service.service.UserService;
import javax.validation.Valid;
import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public User createUser(@Valid @RequestBody UserAuth user) {
        return userService.create(user);
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @RequestMapping(path = "/current", method = RequestMethod.PUT)
    public User updateCurrentUser(Principal principal, @Valid @RequestBody User user) {
        return userService.update(principal.getName(), user);
    }

    @RequestMapping(path = "/current", method = RequestMethod.DELETE)
    public void deleteCurrentUser(Principal principal) {
        userService.delete(principal.getName());
    }
}