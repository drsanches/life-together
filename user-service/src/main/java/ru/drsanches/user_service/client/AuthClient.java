package ru.drsanches.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.drsanches.user_service.data.ChangeUsernameDTO;
import ru.drsanches.user_service.data.UserAuth;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.POST, value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createUser(UserAuth userAuth);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void deleteUser(@PathVariable("id") String id);

    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void changeUsername(ChangeUsernameDTO changeUsernameDTO);
}