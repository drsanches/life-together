package ru.drsanches.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ru.drsanches.common.dto.ChangeUsernameDTO;
import ru.drsanches.common.dto.DisableUserDTO;
import ru.drsanches.user_service.client.AuthClient;
import ru.drsanches.user_service.data.dto.UserDTO;
import ru.drsanches.user_service.data.user.User;
import ru.drsanches.user_service.data.dto.UserAuthDTO;
import ru.drsanches.user_service.data.user.UserRepository;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AuthClient authClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private UserConverter userConverter;

    //TODO: Make transaction
    public UserDTO create(UserAuthDTO userAuthDTO) {
        checkNonexistentUser(userAuthDTO.getUsername());
        String id = UUID.randomUUID().toString();
        userAuthDTO.setId(id);
        authClient.createUser(userAuthDTO);
        User user = new User(id);
        user.setUsername(userAuthDTO.getUsername());
        userRepository.save(user);
        log.info("New user has been created: {}", user.toString());
        return userConverter.convertToDTO(user);
    }

    public UserDTO findByUsername(String username) {
        return userConverter.convertToDTO(getUserIfExists(username));
    }

    //TODO: Make transaction
    public UserDTO update(String username, UserDTO userDTO) {
        User current = getUserIfExists(username);
        if (!StringUtils.isEmpty(userDTO.getUsername()) && !userDTO.getUsername().equals(username)) {
            checkNonexistentUser(userDTO.getUsername());
            current.setUsername(userDTO.getUsername());
            authClient.changeUsername(new ChangeUsernameDTO(username, userDTO.getUsername()));
            log.info("Change username request was sent to auth-service: old username = {}, new username = {}", username, userDTO.getUsername());
        }
        current.setFirstName(userDTO.getFirstName());
        current.setLastName(userDTO.getLastName());
        userRepository.save(current);
        log.info("User has been updated: {}", current.toString());
        return userConverter.convertToDTO(current);
    }

    //TODO: Make transaction
    public void disable(String username) {
        User current = getUserIfExists(username);
        String newUsername = current.getUsername() + "_" + UUID.randomUUID().toString();
        authClient.disableUser(new DisableUserDTO(current.getId(), username, newUsername));
        friendsService.disableUser(username);
        current.getOutgoingRequests().clear();
        current.getIncomingRequests().clear();
        current.setUsername(newUsername);
        current.setEnable(false);
        userRepository.save(current);
        log.info("User has been disabled: {}. Old username: {}", current.toString(), username);
    }

    private User getUserIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "Can't find user with username = " + username);
        Assert.isTrue(user.get().isEnabled(), "Can't find user with username = " + username);
        return user.get();
    }

    private void checkNonexistentUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isEmpty(), "User with username '" + username + "' already exists");
    }
}