package ru.drsanches.debts_service.data.transaction;

import org.springframework.data.repository.CrudRepository;
import java.util.Set;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Set<Transaction> findByFromUserId(String fromUserId);

    Set<Transaction> findByToUserId(String toUserId);
}