package ru.drsanches.auth_service.data.dto;

public class ChangePasswordDTO {

    String oldPassword;

    String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}