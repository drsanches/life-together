package ru.drsanches.debts_service.data.amount;

import java.io.Serializable;

public class TotalAmountKey implements Serializable {
    
    private String fromUserId;

    private String toUserId;

    public TotalAmountKey() {}

    public TotalAmountKey(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}