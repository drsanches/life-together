package ru.drsanches.user_service.data.dto;

public class UserInfoDTO {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    public UserInfoDTO(String id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}