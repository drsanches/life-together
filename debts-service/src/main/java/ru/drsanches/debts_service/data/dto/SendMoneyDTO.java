package ru.drsanches.debts_service.data.dto;

import java.util.List;

public class SendMoneyDTO {

    private List<String> toUserIdList;

    private int money;

    public SendMoneyDTO() {}

    public SendMoneyDTO(List<String> toUserIdList, int money) {
        this.toUserIdList = toUserIdList;
        this.money = money;
    }

    public List<String> getToUserIdList() {
        return toUserIdList;
    }

    public int getMoney() {
        return money;
    }
}