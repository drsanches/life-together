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
        log.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
    }

    public void disable(String userId, String newUsername) {
        User user = getUserByIdIfExists(userId);
        String oldUsername = user.getUsername();
        user.setUsername(newUsername);
        user.setEnable(false);
        userRepository.save(user);
        log.info("user has been disabled: id={}, oldUsername={}, newUsername={}", user.getId(), oldUsername, newUsername);
    }

    public void changeUsername(String oldUsername, String newUsername) {
        User current = getUserByUsernameIfExists(oldUsername);
        checkNonexistentUser(newUsername);
        current.setUsername(newUsername);
        userRepository.save(current);
        log.info("username has been changed: id={}, username={}", current.getId(), current.getUsername());
    }

    private User getUserByUsernameIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "can't find user: username=" + username);
        Assert.isTrue(user.get().isEnabled(), "can't find user: username=" + username);
        return user.get();
    }

    private User getUserByIdIfExists(String userId) {
        Optional<User> user = userRepository.findById(userId);
        Assert.isTrue(user.isPresent(), "can't find user: id=" + userId);
        Assert.isTrue(user.get().isEnabled(), "can't find user: id=" + userId);
        return user.get();
    }

    private void checkNonexistentUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isEmpty(), "user already exists: " + username);
    }
}