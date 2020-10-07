package ru.drsanches.user_service.data.friends;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.drsanches.user_service.data.user.User;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FriendsRepository extends CrudRepository<Friends, String> {

    Set<Friends> findByFromUser(User fromUser);

    Set<Friends> findByToUser(User toUser);

    Optional<Friends> findByFromUserAndToUser(User fromUser, User toUser);
}