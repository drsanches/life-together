package ru.drsanches.debts_service.data.amount;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

//TODO: Add validation
@Entity
@IdClass(AmountKey.class)
public class Amount {

    @Id
    private String fromUserId;

    @Id
    private String toUserId;

    @Column
    private int totalAmount;

    public Amount() {}

    public Amount(String fromUserId, String toUserId, int totalAmount) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.totalAmount = totalAmount;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public int getAmount() {
        return totalAmount;
    }

    public void setAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}