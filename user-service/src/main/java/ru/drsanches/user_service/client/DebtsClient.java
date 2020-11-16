package ru.drsanches.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.drsanches.common.dto.FriendsDTO;

//TODO: Add load distribution
@FeignClient(name = "debts-service")
public interface DebtsClient {

    @RequestMapping(path = "debts/addFriends", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addFriends(FriendsDTO friendsDTO);
}