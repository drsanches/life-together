package ru.drsanches.auth_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.drsanches.auth_service.data.User;
import ru.drsanches.auth_service.data.UserRepository;
import java.util.Optional;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    public void create(User user) {
        checkNonexistentUser(user.getUsername());
        String hash = encoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setEnable(true);
        userRepository.save(user);
        log.info("New user has been created: {}", user.toString());
    }

    public void disable(String userId, String newUsername) {
        User user = getUserByIdIfExists(userId);
        String oldUsername = user.getUsername();
        user.setUsername(newUsername);
        user.setEnable(false);
        userRepository.save(user);
        log.info("User has been disabled: {}. Old username: {}", user.toString(), oldUsername);
    }

    public void changeUsername(String oldUsername, String newUsername) {
        User current = getUserByUsernameIfExists(oldUsername);
        checkNonexistentUser(newUsername);
        current.setUsername(newUsername);
        userRepository.save(current);
        log.info("Username has been changed: {}. Old username: {}", current.toString(), oldUsername);
    }

    private User getUserByUsernameIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "Can't find user with username = " + username);
        Assert.isTrue(user.get().isEnabled(), "Can't find user with username = " + username);
        return user.get();
    }

    private User getUserByIdIfExists(String userId) {
        Optional<User> user = userRepository.findById(userId);
        Assert.isTrue(user.isPresent(), "Can't find user with id = " + userId);
        Assert.isTrue(user.get().isEnabled(), "Can't find user with id = " + userId);
        return user.get();
    }

    private void checkNonexistentUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isEmpty(), "User with username '" + username + "' already exists");
    }
}