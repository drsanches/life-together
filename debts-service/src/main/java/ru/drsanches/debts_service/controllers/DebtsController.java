package ru.drsanches.debts_service.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.common.dto.FriendsDTO;
import ru.drsanches.debts_service.data.dto.DebtsDTO;
import ru.drsanches.debts_service.data.dto.SendMoneyDTO;
import ru.drsanches.debts_service.service.MoneyService;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/debts")
public class DebtsController {

    private final Logger log = LoggerFactory.getLogger(DebtsController.class);

    @Autowired
    MoneyService moneyService;

    @PreAuthorize("#oauth2.hasScope('server')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/addFriends", method = RequestMethod.POST)
    public void addFriends(@RequestBody FriendsDTO friendsDTO) {
        moneyService.addFriends(friendsDTO);
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED) //TODO: 200 or 201?
    public void sendMoney(OAuth2Authentication authentication, @RequestBody SendMoneyDTO sendMoneyDTO) {
        moneyService.sendMoney(getUserId(authentication), sendMoneyDTO);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public DebtsDTO getDebts(OAuth2Authentication authentication) {
        return moneyService.getDebts(getUserId(authentication));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ IllegalArgumentException.class })
    public String handleException(Exception e) {
        log.error("Exception was successfully handled: " + e.getClass() + ": " + e.getMessage(), e);
        return e.getMessage() == null ? "Wrong request data" : e.getMessage();
    }

    private String getUserId(OAuth2Authentication authentication) {
        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) authentication.getUserAuthentication().getDetails();
        LinkedHashMap<String, String> principal = (LinkedHashMap<String, String>) details.get("principal");
        return principal.get("id");
    }
}