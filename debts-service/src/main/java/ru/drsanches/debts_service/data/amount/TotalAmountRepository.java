package ru.drsanches.debts_service.data.amount;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface TotalAmountRepository extends CrudRepository<TotalAmount, TotalAmountKey> {

    Set<TotalAmount> findByFromUserId(String fromUserId);

    Set<TotalAmount> findByToUserId(String toUserId);
}