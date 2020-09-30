package ru.drsanches.auth_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.auth_service.data.ChangeUsernameDTO;
import ru.drsanches.auth_service.service.UserService;
import ru.drsanches.auth_service.data.User;
import javax.validation.Valid;
import java.security.Principal;

@RestController
public class UserController {

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
    public void createUser(@Valid @RequestBody User user) {
        userService.create(user);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String username) {
        removeTokens(username);
        userService.delete(username);
    }

    //TODO: use principal for user id
    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public void changeUsername(@Valid @RequestBody ChangeUsernameDTO changeUsernameDTO) {
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
}