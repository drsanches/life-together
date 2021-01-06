package ru.drsanches.user_service.service;

import org.springframework.stereotype.Service;
import ru.drsanches.user_service.data.dto.UserInfoDTO;
import ru.drsanches.user_service.data.user.User;

@Service
public class UserConverter {

    UserInfoDTO convertToDTO(User user) {
        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}