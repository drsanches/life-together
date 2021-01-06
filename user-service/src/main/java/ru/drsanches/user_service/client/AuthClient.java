package ru.drsanches.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.drsanches.common.dto.ChangeUsernameDTO;
import ru.drsanches.common.dto.DisableUserDTO;
import ru.drsanches.user_service.data.dto.CreateUserDTO;

//TODO: Add load distribution
@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(value = "/auth/createUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void createUser(CreateUserDTO userAuth);

    @RequestMapping(value = "/auth/current", method = RequestMethod.DELETE)
    void disableUser(DisableUserDTO disableUserDTO);

    @RequestMapping(value = "/auth/current/changeUsername", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void changeUsername(ChangeUsernameDTO changeUsernameDTO);
}