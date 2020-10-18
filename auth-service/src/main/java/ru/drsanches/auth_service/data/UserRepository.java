package ru.drsanches.auth_service.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    //TODO: is it necessary?
    @Query("SELECT * FROM user_profile WHERE enabled = true")
    Optional<User> findEnabled();

    @Query("SELECT * FROM user_profile WHERE enabled = false")
    Optional<User> findDisabled();
}