package ru.drsanches.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ru.drsanches.user_service.client.AuthClient;
import ru.drsanches.user_service.data.dto.ChangeUsernameDTO;
import ru.drsanches.user_service.data.dto.UserDTO;
import ru.drsanches.user_service.data.user.User;
import ru.drsanches.user_service.data.user.UserAuth;
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

    public UserDTO create(UserAuth userAuth) {
        checkNonexistentUser(userAuth.getUsername());
        String id = UUID.randomUUID().toString();
        userAuth.setId(id);
        authClient.createUser(userAuth);
        User user = new User(id);
        user.setUsername(userAuth.getUsername());
        userRepository.save(user);
        log.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
        return userConverter.convertToDTO(user);
    }

    public UserDTO findByUsername(String username) {
        return userConverter.convertToDTO(getUserIfExists(username));
    }

    public UserDTO update(String username, UserDTO user) {
        User current = getUserIfExists(username);
        if (!StringUtils.isEmpty(user.getUsername()) && !user.getUsername().equals(username)) {
            checkNonexistentUser(user.getUsername());
            current.setUsername(user.getUsername());
            authClient.changeUsername(new ChangeUsernameDTO(username, user.getUsername()));
        }
        current.setFirstName(user.getFirstName());
        current.setLastName(user.getLastName());
        userRepository.save(current);
        log.info("user has been saved: id={}, username={}", user.getId(), user.getUsername());
        return userConverter.convertToDTO(current);
    }

    public void delete(String username) {
        User current = getUserIfExists(username);
        authClient.deleteUser(current.getUsername());
        friendsService.deleteUser(current.getUsername());
        userRepository.delete(current);
        log.info("user has been deleted: id={}, username={}", current.getId(), current.getUsername());
    }

    private User getUserIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "can't find user: username=" + username);
        return user.get();
    }

    private void checkNonexistentUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isEmpty(), "user already exists: " + username);
    }
}