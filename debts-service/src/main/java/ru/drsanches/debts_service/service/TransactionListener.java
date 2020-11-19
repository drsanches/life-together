package ru.drsanches.debts_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.debts_service.data.amount.TotalAmount;
import ru.drsanches.debts_service.data.amount.TotalAmountKey;
import ru.drsanches.debts_service.data.amount.TotalAmountRepository;
import ru.drsanches.debts_service.data.transaction.Transaction;
import javax.persistence.PrePersist;
import java.util.Optional;

@Service
public class TransactionListener {

    private final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    @Autowired
    TotalAmountRepository totalAmountRepository;

    @PrePersist
    public void audit(Transaction transaction) {
        Optional<TotalAmount> amount = totalAmountRepository.findById(new TotalAmountKey(transaction.getFromUserId(), transaction.getToUserId()));
        //TODO: Think about exception and logging
        amount.ifPresentOrElse((record) -> {
            record.setAmount(record.getAmount() + transaction.getAmount());
            totalAmountRepository.save(record);
            log.info("Amount update: " + record);
        }, () -> log.error("Amount for {} does not exist", transaction));
    }
}