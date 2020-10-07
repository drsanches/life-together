package ru.drsanches.user_service.data.friends;

import ru.drsanches.user_service.data.user.User;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FriendsKey implements Serializable {

    @Column(name = "fromUserId")
    private String fromUserId;

    @Column(name = "toUserId")
    private String toUserId;

    public FriendsKey() {}

    public FriendsKey(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public FriendsKey(User fromUser, User toUser) {
        this.fromUserId = fromUser.getId();
        this.toUserId = toUser.getId();
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}