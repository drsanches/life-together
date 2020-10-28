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
        log.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
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
        }
        current.setFirstName(userDTO.getFirstName());
        current.setLastName(userDTO.getLastName());
        userRepository.save(current);
        log.info("user has been updated: id={}, username={}", current.getId(), userDTO.getUsername());
        return userConverter.convertToDTO(current);
    }

    //TODO: Make transaction
    public void disable(String username) {
        User current = getUserIfExists(username);
        String newUsername = current.getUsername() + "_" + UUID.randomUUID().toString();
        authClient.disableUser(new DisableUserDTO(current.getId(), username, newUsername));
        friendsService.disableUser(username);
        current.setUsername(newUsername);
        current.setEnable(false);
        userRepository.save(current);
        log.info("user has been disabled: id={}, oldUsername={}, newUsername={}", current.getId(), username, newUsername);
    }

    private User getUserIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "can't find user: username=" + username);
        Assert.isTrue(user.get().isEnabled(), "can't find user: username=" + username);
        return user.get();
    }

    private void checkNonexistentUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isEmpty(), "user already exists: " + username);
    }
}