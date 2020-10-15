package ru.drsanches.auth_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.auth_service.service.UserService;
import ru.drsanches.auth_service.data.User;
import ru.drsanches.common.dto.ChangeUsernameDTO;
import ru.drsanches.common.dto.DisableUserDTO;
import java.security.Principal;

@RestController
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTokenStore tokenStore;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestBody User user) {
        userService.create(user);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public void disableUser(@RequestBody DisableUserDTO disableUserDTO) {
        removeTokens(disableUserDTO.getOldUsername());
        userService.disable(disableUserDTO.getId(), disableUserDTO.getNewUsername());
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
        removeTokens(changeUsernameDTO.getOldUsername());
        userService.changeUsername(changeUsernameDTO.getOldUsername(), changeUsernameDTO.getNewUsername());
    }

    @RequestMapping(value = "/log_out", method = RequestMethod.GET)
    public void logout(Principal principal) {
        removeTokens(principal.getName());
    }

    private void removeTokens(String username) {
        tokenStore.findTokensByUserName(username).forEach(token -> {
            tokenStore.removeRefreshToken(token.getRefreshToken());
            tokenStore.removeAccessToken(token);
        });
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ IllegalArgumentException.class })
    public String handleException(Exception e) {
        log.error("Exception was successfully handled: " + e.getClass() + ": " + e.getMessage(), e);
        return e.getMessage() == null ? "Wrong request data" : e.getMessage();
    }
}