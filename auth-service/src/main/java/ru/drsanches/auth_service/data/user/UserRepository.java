package ru.drsanches.auth_service.data.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);
}