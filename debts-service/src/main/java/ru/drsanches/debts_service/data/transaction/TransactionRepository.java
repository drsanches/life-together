package ru.drsanches.debts_service.data.transaction;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.debts_service.data.amount.TotalAmount;
import java.util.Set;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Set<TotalAmount> findByFromUserId(String fromUserId);

    Set<TotalAmount> findByToUserId(String toUserId);
}