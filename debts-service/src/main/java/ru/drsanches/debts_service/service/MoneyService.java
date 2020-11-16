package ru.drsanches.debts_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.drsanches.common.dto.FriendsDTO;
import ru.drsanches.debts_service.data.amount.Amount;
import ru.drsanches.debts_service.data.amount.AmountKey;
import ru.drsanches.debts_service.data.amount.AmountRepository;
import ru.drsanches.debts_service.data.dto.DebtsDTO;
import ru.drsanches.debts_service.data.dto.SendMoneyDTO;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

//TODO: delete the record if the user is no longer a friend and there is no debt
@Service
public class MoneyService {

    private final Logger log = LoggerFactory.getLogger(MoneyService.class);

    @Autowired
    private AmountRepository amountRepository;

    public void addFriends(FriendsDTO friendsDTO) {
        if (!amountExists(friendsDTO.getUserId1(), friendsDTO.getUserId2())) {
            amountRepository.save(new Amount(friendsDTO.getUserId1(), friendsDTO.getUserId2(), 0));
            log.info("New amount has been created: fromUser=" + friendsDTO.getUserId1() + ", toUser=" + friendsDTO.getUserId2());
        }
        if (!amountExists(friendsDTO.getUserId2(), friendsDTO.getUserId1())) {
            amountRepository.save(new Amount(friendsDTO.getUserId2(), friendsDTO.getUserId1(), 0));
            log.info("New amount has been created: fromUser=" + friendsDTO.getUserId2() + ", toUser=" + friendsDTO.getUserId1());
        }
    }

    public void sendMoney(String fromUserId, SendMoneyDTO sendMoneyDTO) {
        Assert.isTrue(sendMoneyDTO.getMoney() > 0, "Amount must be positive: amount=" + sendMoneyDTO.getMoney());
        sendMoneyDTO.getToUserIdList().forEach(toUserId -> {
            if (!fromUserId.equals(toUserId)) {
                getAmountIfExists(fromUserId, toUserId);
            }
        });
        //TODO: think about splitting
        int money = sendMoneyDTO.getMoney() / sendMoneyDTO.getToUserIdList().size();
        sendMoneyDTO.getToUserIdList().forEach(toUserId -> {
            if (!fromUserId.equals(toUserId)) {
                sendMoney(fromUserId, toUserId, money);
            }
        });
    }

    public DebtsDTO getDebts(String userId) {
        Set<Amount> userDebts = amountRepository.findByToUserId(userId);
        Set<Amount> toUserDebts = amountRepository.findByFromUserId(userId);
        HashMap<String, Integer> userDebtsMap = new HashMap<>();
        HashMap<String, Integer> toUserDebtsMap = new HashMap<>();
        userDebts.forEach(debt -> userDebtsMap.put(debt.getFromUserId(), debt.getAmount()));
        toUserDebts.forEach(debt -> toUserDebtsMap.put(debt.getToUserId(), debt.getAmount()));
        DebtsDTO debtsDTO = new DebtsDTO();
        for (String key: userDebtsMap.keySet()) {
            int userDebt = userDebtsMap.get(key);
            int toUserDebt = toUserDebtsMap.get(key);
            if (userDebt > toUserDebt) {
                debtsDTO.addUserDebt(key, userDebt - toUserDebt);
            } else if (userDebt < toUserDebt) {
                debtsDTO.addToUserDebt(key, toUserDebt - userDebt);
            }
        }
        return debtsDTO;
    }

    private void sendMoney(String fromUserId, String toUserId, int money) {
        Assert.isTrue(money > 0, "Amount must be positive: amount=" + money);
        Amount amount = getAmountIfExists(fromUserId, toUserId);
        amount.setAmount(amount.getAmount() + money);
        amountRepository.save(amount);
        log.info("User '" + fromUserId + " sent '" + money + "' to user '" + toUserId + "'");
    }

    private Amount getAmountIfExists(String fromUser, String toUser) {
        Optional<Amount> amount = amountRepository.findById(new AmountKey(fromUser, toUser));
        Assert.isTrue(amount.isPresent(), "Amount does not exists: fromUser=" + fromUser + ", toUser=" + toUser);
        return amount.get();
    }

    private boolean amountExists(String fromUser, String toUser) {
        Optional<Amount> amount = amountRepository.findById(new AmountKey(fromUser, toUser));
        return amount.isPresent();
    }
}