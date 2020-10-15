package ru.drsanches.user_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.user_service.data.dto.UserDTO;
import ru.drsanches.user_service.service.FriendsService;
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping(value = "/friends")
public class FriendsController {

    private final Logger log = LoggerFactory.getLogger(FriendsController.class);

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Set<UserDTO> getFriends(Principal principal) {
        return friendsService.getFriends(principal.getName());
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.POST)
    public void sendRequest(Principal principal, @PathVariable String username) {
        friendsService.sendRequest(principal.getName(), username);
    }

    @RequestMapping(path = "/{username}", method = RequestMethod.DELETE)
    public void removeRequest(Principal principal, @PathVariable String username) {
        friendsService.removeRequest(principal.getName(), username);
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public Set<UserDTO> getIncomingRequests(Principal principal) {
        return friendsService.getIncomingRequests(principal.getName());
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public Set<UserDTO> getOutgoingRequests(Principal principal) {
        return friendsService.getOutgoingRequests(principal.getName());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ IllegalArgumentException.class })
    public String handleException(Exception e) {
        log.error("Exception was successfully handled: " + e.getClass() + ": " + e.getMessage(), e);
        return e.getMessage() == null ? "Wrong request data" : e.getMessage();
    }
}