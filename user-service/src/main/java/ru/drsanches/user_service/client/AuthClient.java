package ru.drsanches.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.drsanches.user_service.data.dto.ChangeUsernameDTO;
import ru.drsanches.user_service.data.user.UserAuth;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.POST, value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createUser(UserAuth userAuth);

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    void deleteUser(@PathVariable("username") String username);

    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void changeUsername(ChangeUsernameDTO changeUsernameDTO);
}