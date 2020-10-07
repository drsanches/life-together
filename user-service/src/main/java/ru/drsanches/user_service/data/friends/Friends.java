package ru.drsanches.user_service.data.friends;

import ru.drsanches.user_service.data.user.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class Friends {

    @EmbeddedId
    private FriendsKey id;

    @ManyToOne
    @MapsId("fromUser")
    @JoinColumn(name = "fromUserId")
    private User fromUser;

    @ManyToOne
    @MapsId("toUser")
    @JoinColumn(name = "toUserId")
    private User toUser;

    public Friends() {}

    public Friends(User fromUser, User toUser) {
        this.id = new FriendsKey(fromUser, toUser);
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }
}