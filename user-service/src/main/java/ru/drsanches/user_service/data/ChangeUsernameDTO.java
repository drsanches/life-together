package ru.drsanches.user_service.data;

public class ChangeUsernameDTO {

    private String userId;

    private String newUsername;

    public ChangeUsernameDTO() {}

    public ChangeUsernameDTO(String userId, String newUsername) {
        this.userId = userId;
        this.newUsername = newUsername;
    }

    public String getUserId() {
        return userId;
    }

    public String getNewUsername() {
        return newUsername;
    }
}