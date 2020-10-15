package ru.drsanches.user_service.service;

import org.springframework.stereotype.Service;
import ru.drsanches.user_service.data.dto.UserDTO;
import ru.drsanches.user_service.data.user.User;

@Service
public class UserConverter {

    UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}