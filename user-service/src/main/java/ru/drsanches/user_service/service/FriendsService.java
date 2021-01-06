package ru.drsanches.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.drsanches.common.dto.FriendsDTO;
import ru.drsanches.user_service.client.DebtsClient;
import ru.drsanches.user_service.data.friends.Friends;
import ru.drsanches.user_service.data.friends.FriendsRepository;
import ru.drsanches.user_service.data.user.User;
import ru.drsanches.user_service.data.dto.UserInfoDTO;
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

    @Autowired
    private DebtsClient debtsClient;

    public Set<UserInfoDTO> getFriends(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return outgoing.stream().filter(incoming::contains).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    public void sendRequest(String fromUsername, String toUsername) {
        Assert.isTrue(!fromUsername.equals(toUsername), "You can't send friend request to yourself");
        User fromUser = getUserIfExists(fromUsername);
        User toUser = getUserIfExists(toUsername);
        Optional<Friends> newFriends = friendsRepository.findByFromUserAndToUser(fromUser, toUser);
        Assert.isTrue(newFriends.isEmpty(), "Friend request from '" + fromUsername + "' to '" + toUsername + "' already exists");
        friendsRepository.save(new Friends(fromUser, toUser));
        log.info("User '{}' sent friend request to '{}'", fromUser.toString(), toUser.toString());
        Optional<Friends> oldFriends = friendsRepository.findByFromUserAndToUser(toUser, fromUser);
        if (oldFriends.isPresent()) {
            debtsClient.addFriends(new FriendsDTO(toUser.getId(), fromUser.getId()));
            log.info("New friends record was sent to debts-service: user1 = '{}', user2 = '{}'", toUser.toString(), fromUser.toString());
        }
    }

    public void removeRequest(String fromUsername, String toUsername) {
        Assert.isTrue(!fromUsername.equals(toUsername), "You can't remove friend request for yourself");
        User fromUser = getUserIfExists(fromUsername);
        User toUser = getUserIfExists(toUsername);
        Optional<Friends> friends = friendsRepository.findByFromUserAndToUser(fromUser, toUser);
        Assert.isTrue(friends.isPresent(), "Friend request from '" + fromUsername + "' to '" + toUsername + "' does not exist");
        friendsRepository.delete(friends.get());
        log.info("User '{}' deleted friend request to '{}'", fromUser.toString(), toUser.toString());
    }

    public void disableUser(String username) {
        User user = getUserIfExists(username);
        friendsRepository.deleteAll(friendsRepository.findByFromUser(user));
        friendsRepository.deleteAll(friendsRepository.findByToUser(user));
        log.info("User '{}' has been deleted from all friends", user.toString());
    }

    public Set<UserInfoDTO> getIncomingRequests(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return incoming.stream().filter(x -> !outgoing.contains(x)).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    public Set<UserInfoDTO> getOutgoingRequests(String username) {
        User user = getUserIfExists(username);
        Set<User> outgoing = user.getOutgoingRequests().stream().map(Friends::getToUser).collect(Collectors.toSet());
        Set<User> incoming = user.getIncomingRequests().stream().map(Friends::getFromUser).collect(Collectors.toSet());
        return outgoing.stream().filter(x -> !incoming.contains(x)).map(userConverter::convertToDTO).collect(Collectors.toSet());
    }

    private User getUserIfExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        Assert.isTrue(user.isPresent(), "Can't find user with username = " + username);
        Assert.isTrue(user.get().isEnabled(), "Can't find user with username = " + username);
        return user.get();
    }
}