package ru.drsanches.debts_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.drsanches.common.dto.FriendsDTO;
import ru.drsanches.debts_service.data.amount.TotalAmount;
import ru.drsanches.debts_service.data.amount.TotalAmountKey;
import ru.drsanches.debts_service.data.amount.TotalAmountRepository;
import ru.drsanches.debts_service.data.dto.DebtsDTO;
import ru.drsanches.debts_service.data.dto.SendMoneyDTO;
import ru.drsanches.debts_service.data.transaction.Transaction;
import ru.drsanches.debts_service.data.transaction.TransactionRepository;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

//TODO: delete the record if the user is no longer a friend and there is no debt
//TODO: make transaction
@Service
public class MoneyService {

    private final Logger log = LoggerFactory.getLogger(MoneyService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TotalAmountRepository totalAmountRepository;

    public void addFriends(FriendsDTO friendsDTO) {
        if (!amountExists(friendsDTO.getUserId1(), friendsDTO.getUserId2())) {
            TotalAmount amount = new TotalAmount(friendsDTO.getUserId1(), friendsDTO.getUserId2(), 0);
            totalAmountRepository.save(amount);
            log.info("New TotalAmount has been created: {}", amount);
        }
        if (!amountExists(friendsDTO.getUserId2(), friendsDTO.getUserId1())) {
            TotalAmount amount = new TotalAmount(friendsDTO.getUserId2(), friendsDTO.getUserId1(), 0);
            totalAmountRepository.save(amount);
            log.info("New TotalAmount has been created: {}", amount);
        }
    }

    public void sendMoney(String fromUserId, SendMoneyDTO sendMoneyDTO) {
        Assert.isTrue(sendMoneyDTO.getMoney() > 0, "Money must be positive: money=" + sendMoneyDTO.getMoney());
        sendMoneyDTO.getToUserIdList().forEach(toUserId -> {
            if (!fromUserId.equals(toUserId)) {
                checkAmountExists(fromUserId, toUserId);
            }
        });
        //TODO: think about splitting
        int money = sendMoneyDTO.getMoney() / sendMoneyDTO.getToUserIdList().size();
        sendMoneyDTO.getToUserIdList().forEach(toUserId -> {
            if (!fromUserId.equals(toUserId)) {
                sendMoney(fromUserId, toUserId, money, sendMoneyDTO.getMessage());
            }
        });
    }

    public DebtsDTO getDebts(String userId) {
        Set<TotalAmount> userDebts = totalAmountRepository.findByToUserId(userId);
        Set<TotalAmount> toUserDebts = totalAmountRepository.findByFromUserId(userId);
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

    public Set<Transaction> getHistory(String userId) {
        Set<Transaction> transactions = transactionRepository.findByFromUserId(userId);
        transactions.addAll(transactionRepository.findByToUserId(userId));
        return transactions;
    }

    private void sendMoney(String fromUserId, String toUserId, int money, String message) {
        Assert.isTrue(money > 0, "Money must be positive: money=" + money);
        checkAmountExists(fromUserId, toUserId);
        Transaction transaction = new Transaction(fromUserId, toUserId, money, message);
        transactionRepository.save(transaction);
        log.info("New transaction: {}", transaction);
    }

    private void checkAmountExists(String fromUser, String toUser) {
        Assert.isTrue(amountExists(fromUser, toUser), "User '" + fromUser + "' can't send money to user '" + toUser + "'");
    }

    private boolean amountExists(String fromUser, String toUser) {
        Optional<TotalAmount> amount = totalAmountRepository.findById(new TotalAmountKey(fromUser, toUser));
        return amount.isPresent();
    }
}