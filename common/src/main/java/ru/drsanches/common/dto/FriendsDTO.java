package ru.drsanches.common.dto;

public class FriendsDTO {

    private String userId1;

    private String userId2;

    public FriendsDTO() {}

    public FriendsDTO(String userId1, String userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getUserId1() {
        return userId1;
    }

    public String getUserId2() {
        return userId2;
    }
}