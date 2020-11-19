package ru.drsanches.debts_service.data.dto;

import java.util.List;

public class SendMoneyDTO {

    private List<String> toUserIdList;

    private int money;

    private String message;

    public SendMoneyDTO() {}

    public SendMoneyDTO(List<String> toUserIdList, int money, String message) {
        this.toUserIdList = toUserIdList;
        this.money = money;
        this.message = message;
    }

    public List<String> getToUserIdList() {
        return toUserIdList;
    }

    public int getMoney() {
        return money;
    }

    public String getMessage() {
        return message;
    }
}