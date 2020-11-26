package ru.drsanches.tests.user_service.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import ru.drsanches.tests.Utils
import spock.lang.Specification

class TestSendMoney extends Specification {

    String PATH = "/debts/send"

    def "success money send to one user"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def userId1 = RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()
        def message = DataGenerator.createValidMessage()

        when: "send is called"
        def dateBefore = new Date()
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId2],
                        money: money,
                        message: message],
                requestContentType : ContentType.JSON)
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts is correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == JSONArray.fromObject(['userId': userId2, 'debt': money])
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == JSONArray.fromObject(['userId': userId1, 'debt': money])

        and: "history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 1
        assert history2.size() == 1
        def transaction1 = Utils.findTransaction(history1, userId1, userId2, money, message)
        def transaction2 = Utils.findTransaction(history2, userId1, userId2, money, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"], dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"], dateAfter)
    }

    def "success money send to both users"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def userId1 = RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()
        def message = DataGenerator.createValidMessage()

        when: "send is called"
        def dateBefore = new Date()
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId1, userId2],
                        money: money,
                        message: message],
                requestContentType : ContentType.JSON)
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "debts is correct"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == JSONArray.fromObject(['userId': userId2, 'debt': (int) money / 2])
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == JSONArray.fromObject(['userId': userId1, 'debt': (int) money / 2])

        and: "history is correct"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 1
        assert history2.size() == 1
        def transaction1 = Utils.findTransaction(history1, userId1, userId2, money / 2 as int, message)
        def transaction2 = Utils.findTransaction(history2, userId1, userId2, money / 2 as int, message)
        assert transaction1 != null
        assert transaction2 != null
        assert Utils.checkTimestamp(dateBefore, transaction1["timestamp"], dateAfter)
        assert Utils.checkTimestamp(dateBefore, transaction2["timestamp"], dateAfter)
    }

    def "invalid money send"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0

        where:
        money << [0, -1]
    }

    def "without friend requests money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    def "with outgoing friend request money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        def token1 = RequestUtils.getToken(username1, password1)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def money = DataGenerator.createValidMoney()

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    def "with incoming friend request money send"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        def token1 = RequestUtils.getToken(username1, password1)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def money = DataGenerator.createValidMoney()

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [userId2],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        def debts2 = RequestUtils.getDebts(username2, password2)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()
        assert debts2["toUserDebts"] == new JSONArray()
        assert debts2["userDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        def history2 = RequestUtils.getHistory(username2, password2)
        assert history1.size() == 0
        assert history2.size() == 0
    }

    //TODO: Implement test
    def "send money back to a user who is no longer a friend"() {}

    //TODO: Implement test
    def "disable user"() {}

    def "invalid user id money send"() {
        given: "user and invalid user id"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()
        def toUserId = UUID.randomUUID().toString()

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body:  [toUserIdList: [toUserId],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 0
    }

    def "invalid token money send"() {
        given: "user with invalid token"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def toUserId = RequestUtils.createUser(username1, password1)["id"]
        def token = UUID.randomUUID().toString()
        def money = DataGenerator.createValidMoney()

        when: "send is called"
        RequestUtils.getDebtsRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [toUserIdList: [toUserId],
                        money: money],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "debts is empty"
        def debts1 = RequestUtils.getDebts(username1, password1)
        assert debts1["userDebts"] == new JSONArray()
        assert debts1["toUserDebts"] == new JSONArray()

        and: "history is empty"
        def history1 = RequestUtils.getHistory(username1, password1)
        assert history1.size() == 0
    }
}