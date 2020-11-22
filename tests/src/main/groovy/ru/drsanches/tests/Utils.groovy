package ru.drsanches.tests

import net.sf.json.JSONArray
import net.sf.json.JSONObject

class Utils {

    static boolean historyContainsTransaction(JSONArray history, String fromUserId, String toUserId, int money, String message) {
        for (JSONObject transaction: history) {
            if (transaction["fromUserId"] == fromUserId
                    && transaction["toUserId"] == toUserId
                    && transaction["amount"] == money
                    && transaction["message"] == message) {
                return true
            }
        }
        return false
    }

    static boolean checkTimestamp(Date timestampBeforeTransaction, Date timestamp) {
        return timestamp.after(timestampBeforeTransaction) && timestamp.before(new Date())
    }
}