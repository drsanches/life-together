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

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    public void create(User user) {
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        existing.ifPresent(it -> {throw new IllegalArgumentException("User already exists: " + it.getUsername());});
        String hash = ENCODER.encode(user.getPassword());
        user.setPassword(hash);
        userRepository.save(user);
        LOG.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
    }

    public void delete(String id) {
        //TODO: Delete auth token
        Optional<User> current = userRepository.findById(id);
        Assert.isTrue(current.isPresent(), "can't find user: id=" + id);
        userRepository.delete(current.get());
        LOG.info("user has been deleted: id={}, username={}", current.get().getId(), current.get().getUsername());
    }

    public void changeUsername(String id, String newUsername) {
        //TODO: Update auth token
        Optional<User> current = userRepository.findById(id);
        Assert.isTrue(current.isPresent(), "can't find user: id=" + id);
        Optional<User> another = userRepository.findByUsername(newUsername);
        Assert.isTrue(another.isEmpty(), "user with username '" + newUsername + "' already exists");
        User currentUser = current.get();
        currentUser.setUsername(newUsername);
        userRepository.save(currentUser);
        LOG.info("username has been changed: id={}, username={}", currentUser.getId(), currentUser.getUsername());
    }
}