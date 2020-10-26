package ru.drsanches.user_service.data.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

//TODO: do something with repository name
@Repository("ru.drsanches.user_service.data.user.UserRepository")
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);
}