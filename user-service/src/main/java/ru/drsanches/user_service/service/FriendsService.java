package ru.drsanches.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.drsanches.user_service.data.friends.Friends;
import ru.drsanches.user_service.data.friends.FriendsRepository;
import ru.drsanches.user_service.data.user.User;
import ru.drsanches.user_service.data.dto.UserDTO;
import ru.drsanches.user_service.data.user.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private final Logger log = LoggerFactory.getLogger(FriendsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private UserConverter userConverter;

    public Set<UserDTO> getFriends(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return outgoing.stream().filter(incoming::contains).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    public void sendRequest(String fromUsername, String toUsername) {
        Assert.isTrue(!fromUsername.equals(toUsername), "users are equal: username=" + fromUsername);
        User fromUser = getUserIfExists(fromUsername);
        User toUser = getUserIfExists(toUsername);
        Optional<Friends> friends = friendsRepository.findByFromUserAndToUser(fromUser, toUser);
        Assert.isTrue(friends.isEmpty(), "friends already exists: fromUsername=" + fromUsername + ", toUsername=" + toUsername);
        friendsRepository.save(new Friends(fromUser, toUser));
        log.info("User '{}' sent friend request to '{}'", fromUsername, toUsername);
    }

    public void removeRequest(String fromUsername, String toUsername) {
        Assert.isTrue(!fromUsername.equals(toUsername), "users are equal: username=" + fromUsername);
        User fromUser = getUserIfExists(fromUsername);
        User toUser = getUserIfExists(toUsername);
        Optional<Friends> friends = friendsRepository.findByFromUserAndToUser(fromUser, toUser);
        Assert.isTrue(friends.isPresent(), "friends does not exist: fromUsername=" + fromUsername + ", toUsername=" + toUsername);
        friendsRepository.delete(friends.get());
        log.info("User '{}' deleted friend request to '{}'", fromUsername, toUsername);
    }

    public void deleteUser(String username) {
        User user = getUserIfExists(username);
        friendsRepository.deleteAll(friendsRepository.findByFromUser(user));
        friendsRepository.deleteAll(friendsRepository.findByToUser(user));
        log.info("user '{}' has been deleted from friends", username);
    }

    public Set<UserDTO> getIncomingRequests(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return incoming.stream().filter(x -> !outgoing.contains(x)).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    public Set<UserDTO> getOutgoingRequests(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return outgoing.stream().filter(x -> !incoming.contains(x)).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    private User getUserIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "can't find user: username=" + username);
        return user.get();
    }
}