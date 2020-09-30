package ru.drsanches.user_service.data;

public class ChangeUsernameDTO {

    private String oldUsername;

    private String newUsername;

    public ChangeUsernameDTO() {}

    public ChangeUsernameDTO(String oldUsername, String newUsername) {
        this.oldUsername = oldUsername;
        this.newUsername = newUsername;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }
}