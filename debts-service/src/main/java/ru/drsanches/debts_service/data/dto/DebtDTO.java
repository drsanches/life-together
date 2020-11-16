package ru.drsanches.debts_service.data.dto;

public class DebtDTO {

    private String userId;

    private int debt;

    public DebtDTO() {}

    public DebtDTO(String userId, int debt) {
        this.userId = userId;
        this.debt = debt;
    }

    public String getUserId() {
        return userId;
    }

    public int getDebt() {
        return debt;
    }
}