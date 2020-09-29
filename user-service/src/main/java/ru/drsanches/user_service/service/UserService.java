package ru.drsanches.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import ru.drsanches.user_service.client.AuthClient;
import ru.drsanches.user_service.data.ChangeUsernameDTO;
import ru.drsanches.user_service.data.User;
import ru.drsanches.user_service.data.UserAuth;
import ru.drsanches.user_service.data.UserRepository;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AuthClient authClient;

    @Autowired
    private UserRepository userRepository;

    public User create(UserAuth userAuth) {
        Optional<User> existing = userRepository.findByUsername(userAuth.getUsername());
        Assert.isTrue(existing.isEmpty(), "account already exists: " + userAuth.getUsername());
        String id = UUID.randomUUID().toString();
        userAuth.setId(id);
        authClient.createUser(userAuth);
        User user = new User(id);
        user.setUsername(userAuth.getUsername());
        userRepository.save(user);
        log.info("new user has been created: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "can't find user: username=" + username);
        return user.get();
    }

    public User update(String username, User user) {
        Optional<User> current = userRepository.findByUsername(username);
        Assert.isTrue(current.isPresent(), "can't find user: username=" + username);
        User currentUser = current.get();
        if (!StringUtils.isEmpty(user.getUsername()) && !user.getUsername().equals(currentUser.getUsername())) {
            Optional<User> another = userRepository.findByUsername(user.getUsername());
            Assert.isTrue(another.isEmpty(), "user with username '" + username + "' already exists");
            currentUser.setUsername(user.getUsername());
            authClient.changeUsername(new ChangeUsernameDTO(currentUser.getId(), user.getUsername()));
        }
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        userRepository.save(currentUser);
        log.debug("user has been saved: id={}, username={}", user.getId(), user.getUsername());
        return currentUser;
    }

    public void delete(String username) {
        Optional<User> current = userRepository.findByUsername(username);
        Assert.isTrue(current.isPresent(), "can't find user: username=" + username);
        authClient.deleteUser(current.get().getId());
        userRepository.delete(current.get());
        log.info("user has been deleted: id={}, username={}", current.get().getId(), current.get().getUsername());
    }
}