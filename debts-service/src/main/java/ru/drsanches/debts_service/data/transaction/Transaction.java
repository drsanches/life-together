package ru.drsanches.debts_service.data.transaction;

import ru.drsanches.debts_service.service.TransactionListener;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Entity
@EntityListeners(TransactionListener.class)
public class Transaction {

    @Id
    private String id;

    @Column
    private String fromUserId;

    @Column
    private String toUserId;

    @Column
    private int amount;

    @Column
    private String message;

    @Column
    private Date timestamp;

    public Transaction() {}

    public Transaction(String fromUserId, String toUserId, int amount, String message) {
        this.id = UUID.randomUUID().toString();
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.message = message;
        //TODO: Fix
        this.timestamp = new Date();
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public int getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}