package ru.drsanches.common.dto;

public class DisableUserDTO {

    private String id;

    private String oldUsername;

    private String newUsername;

    public DisableUserDTO() {}

    public DisableUserDTO(String id, String oldUsername, String newUsername) {
        this.id = id;
        this.oldUsername = oldUsername;
        this.newUsername = newUsername;
    }

    public String getId() {
        return id;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }
}