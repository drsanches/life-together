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
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        existing.ifPresent(it -> {throw new IllegalArgumentException("User already exists: " + it.getUsername());});
        String hash = encoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setEnable(true);
        userRepository.save(user);
        log.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
    }

    public void disable(String userId, String newUsername) {
        Optional<User> current = userRepository.findById(userId);
        Assert.isTrue(current.isPresent(), "can't find user: id=" + userId);
        User currentUser = current.get();
        currentUser.setUsername(newUsername);
        currentUser.setEnable(false);
        userRepository.save(currentUser);
        log.info("user has been disabled: id={}, username={}", current.get().getId(), current.get().getUsername());
    }

    public void changeUsername(String oldUsername, String newUsername) {
        Optional<User> current = userRepository.findByUsername(oldUsername);
        Assert.isTrue(current.isPresent(), "can't find user: username=" + oldUsername);
        Optional<User> another = userRepository.findByUsername(newUsername);
        Assert.isTrue(another.isEmpty(), "user already exists: username=" + newUsername);
        User currentUser = current.get();
        currentUser.setUsername(newUsername);
        userRepository.save(currentUser);
        log.info("username has been changed: id={}, username={}", currentUser.getId(), currentUser.getUsername());
    }
}