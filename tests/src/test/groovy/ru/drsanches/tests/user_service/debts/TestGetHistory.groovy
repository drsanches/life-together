package ru.drsanches.tests.user_service.debts

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import ru.drsanches.tests.Utils
import spock.lang.Specification

class TestGetHistory extends Specification {

    def "success history getting"() {
        given: "three friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def username3 = DataGenerator.createValidUsername()
        def password3 = DataGenerator.createValidPassword()
        def userId1 = RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        def userId3 = RequestUtils.createUser(username3, password3)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        RequestUtils.sendFriendRequest(username1, password1, username3)
        RequestUtils.sendFriendRequest(username3, password3, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = 100
        def message1 = DataGenerator.createValidMessage()
        def message2 = DataGenerator.createValidMessage()
        def message3 = DataGenerator.createValidMessage()
        RequestUtils.sendMoney(username1, password1, [userId1, userId2, userId3] as List<String>, money * 3, message1)
        RequestUtils.sendMoney(username2, password2, [userId1, userId2] as List<String>, money * 2, message2)
        RequestUtils.sendMoney(username3, password3, [userId1] as List<String>, money * 2, message3)

        when: "getHistory is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData().size() == 4
        assert Utils.historyContainsTransaction(response.getData(), userId1, userId2, money, message1)
        assert Utils.historyContainsTransaction(response.getData(), userId1, userId3, money, message1)
        assert Utils.historyContainsTransaction(response.getData(), userId2, userId1, money, message2)
        assert Utils.historyContainsTransaction(response.getData(), userId3, userId1, money * 2, message3)
        //TODO: Check timestamp
    }

    def "without friends history getting"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "getHistory is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == new JSONArray()
    }

    def "empty history getting"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "getHistory is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == new JSONArray()
    }

    def "get history with removed friend"() {
        given: "one user with former friend"
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
        RequestUtils.sendMoney(username1, password1, [userId2] as List<String>, money, message)
        RequestUtils.deleteFriend(username2, password2, username1)

        when: "getHistory is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData().size() == 1
        assert Utils.historyContainsTransaction(response.getData(), userId1, userId2, money, message)
        //TODO: Check timestamp
    }

    def "get history with disabled friend"() {
        given: "one user with deleted friend"
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
        RequestUtils.sendMoney(username1, password1, [userId2] as List<String>, money, message)
        RequestUtils.disableUser(username2, password2)

        when: "getHistory is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData().size() == 1
        assert Utils.historyContainsTransaction(response.getData(), userId1, userId2, money, message)
        //TODO: Check timestamp
    }

    def "invalid token history getting"() {
        given: "user with invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "getHistory is called"
        RequestUtils.getDebtsRestClient().get(
                path: "/debts/history",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}