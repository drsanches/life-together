package ru.drsanches.tests

import net.sf.json.JSONArray
import net.sf.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static JSONObject findTransaction(JSONArray history, String fromUserId, String toUserId, int money, String message) {
        for (JSONObject transaction: history) {
            if (transaction["fromUserId"] == fromUserId
                    && transaction["toUserId"] == toUserId
                    && transaction["amount"] == money
                    && transaction["message"] == message) {
                return transaction
            }
        }
        return null
    }

    static boolean checkTimestamp(Date timestampBefore, String timestamp, Date timeAfter) {
        //TODO: refactor
        String tmp = timestamp.replace("T", " ")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSX")
        Date date = df.parse(tmp)
        return date.after(timestampBefore) && date.before(timeAfter)
    }
}