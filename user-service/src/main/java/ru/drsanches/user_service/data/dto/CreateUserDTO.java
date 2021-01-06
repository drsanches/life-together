package ru.drsanches.user_service.data.dto;

import javax.validation.constraints.NotNull;
import java.util.UUID;

//TODO: Add validation
public class CreateUserDTO {

    private String id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    public CreateUserDTO() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}