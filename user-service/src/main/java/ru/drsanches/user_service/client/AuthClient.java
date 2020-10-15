package ru.drsanches.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.drsanches.common.dto.ChangeUsernameDTO;
import ru.drsanches.common.dto.DisableUserDTO;
import ru.drsanches.user_service.data.dto.UserAuthDTO;

//TODO: Add load distribution
@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.POST, value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createUser(UserAuthDTO userAuth);

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    void disableUser(DisableUserDTO disableUserDTO);

    @RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void changeUsername(ChangeUsernameDTO changeUsernameDTO);
}