package ru.drsanches.user_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.user_service.data.dto.CreateUserDTO;
import ru.drsanches.user_service.data.dto.UpdateUserDTO;
import ru.drsanches.user_service.data.dto.UserInfoDTO;
import ru.drsanches.user_service.data.dto.UsernameDTO;
import ru.drsanches.user_service.service.UserService;
import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfoDTO createUser(@RequestBody CreateUserDTO user) {
        return userService.create(user);
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public UserInfoDTO getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @RequestMapping(path = "/current", method = RequestMethod.PUT)
    public UserInfoDTO updateCurrentUser(Principal principal, @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.update(principal.getName(), updateUserDTO);
    }

    @RequestMapping(path = "/current/changeUsername", method = RequestMethod.PUT)
    public UserInfoDTO changeUsername(Principal principal, @RequestBody UsernameDTO usernameDTO) {
        return userService.changeUsername(principal.getName(), usernameDTO.getUsername());
    }

    @RequestMapping(path = "/current", method = RequestMethod.DELETE)
    public void disableCurrentUser(Principal principal) {
        userService.disable(principal.getName());
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.GET)
    public UserInfoDTO getUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ IllegalArgumentException.class })
    public String handleException(Exception e) {
        log.error("Exception was successfully handled: " + e.getClass() + ": " + e.getMessage(), e);
        return e.getMessage() == null ? "Wrong request data" : e.getMessage();
    }
}