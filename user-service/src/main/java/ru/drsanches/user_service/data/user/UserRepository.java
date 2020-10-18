package ru.drsanches.user_service.data.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

//TODO: do something with repository name
@Repository("ru.drsanches.user_service.data.user.UserRepository")
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    //TODO: is it necessary?
    @Query("SELECT * FROM user_profile WHERE enabled = true")
    Optional<User> findEnabled();

    @Query("SELECT * FROM user_profile WHERE enabled = false")
    Optional<User> findDisabled();
}