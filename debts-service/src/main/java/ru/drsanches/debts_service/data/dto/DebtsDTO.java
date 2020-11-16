package ru.drsanches.debts_service.data.dto;

import java.util.LinkedList;
import java.util.List;

public class DebtsDTO {

    private List<DebtDTO> userDebts;

    private List<DebtDTO> toUserDebts;

    public DebtsDTO() {
        this.userDebts = new LinkedList<>();
        this.toUserDebts = new LinkedList<>();
    }

    public List<DebtDTO> getUserDebts() {
        return userDebts;
    }

    public List<DebtDTO> getToUserDebts() {
        return toUserDebts;
    }

    public void addUserDebt(String userId, Integer debt) {
        userDebts.add(new DebtDTO(userId, debt));
    }

    public void addToUserDebt(String userId, Integer debt) {
        toUserDebts.add(new DebtDTO(userId, debt));
    }
}