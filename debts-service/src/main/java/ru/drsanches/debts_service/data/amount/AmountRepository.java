package ru.drsanches.debts_service.data.amount;

import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface AmountRepository extends CrudRepository<Amount, AmountKey> {

    Set<Amount> findByFromUserId(String fromUserId);

    Set<Amount> findByToUserId(String toUserId);
}