package ru.drsanches.debts_service.data.transaction;

import ru.drsanches.debts_service.service.TransactionListener;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@EntityListeners(TransactionListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column
    private String fromUserId;

    @Column
    private String toUserId;

    @Column
    private int amount;

    @Column
    private String message;

    //TODO: Fix
    @Column(columnDefinition = "TIMESTAMP")
    private Date timestamp;

    public Transaction() {}

    public Transaction(String fromUserId, String toUserId, int amount, String message) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.message = message;
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