package ru.drsanches.tests.debts_service

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestGetDebts extends Specification {

    String PATH = "/debts"

    def "success debts getting"() {
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
        RequestUtils.sendMoney(username1, password1, [userId1, userId2, userId3] as List<String>, money * 3, null)
        RequestUtils.sendMoney(username2, password2, [userId1, userId2] as List<String>, money * 2, null)
        RequestUtils.sendMoney(username3, password3, [userId1] as List<String>, money * 2, null)

        when: "getDebts is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "debts is correct"
        assert response.getData()["userDebts"] == JSONArray.fromObject([userId: userId3, debt: money])
        assert response.getData()["toUserDebts"] == new JSONArray()
    }

    def "without friends debts getting"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "getDebts is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "debts is empty"
        assert response.getData()["userDebts"] == new JSONArray()
        assert response.getData()["toUserDebts"] == new JSONArray()
    }

    def "empty debts getting"() {
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

        when: "getDebts is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "debts is empty"
        assert response.getData()["userDebts"] == new JSONArray()
        assert response.getData()["toUserDebts"] == new JSONArray()
    }

    def "get debts with removed friend"() {
        given: "one user with former friend"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()
        RequestUtils.sendMoney(username1, password1, [userId2] as List<String>, money, null)
        RequestUtils.deleteFriend(username2, password2, username1)

        when: "getDebts is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "debts is correct"
        assert response.getData()["userDebts"] == new JSONArray()
        assert response.getData()["toUserDebts"] == JSONArray.fromObject([userId: userId2, debt: money])
    }

    def "get debts with disabled friend"() {
        given: "one user with deleted friend"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)["id"]
        def userId2 = RequestUtils.createUser(username2, password2)["id"]
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)
        def money = DataGenerator.createValidMoney()
        RequestUtils.sendMoney(username1, password1, [userId2] as List<String>, money, null)
        RequestUtils.disableUser(username2, password2)

        when: "getDebts is called"
        HttpResponseDecorator response = RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "debts is correct"
        assert response.getData()["userDebts"] == new JSONArray()
        assert response.getData()["toUserDebts"] == JSONArray.fromObject([userId: userId2, debt: money])
    }

    def "invalid token debts getting"() {
        given: "user with invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "getDebts is called"
        RequestUtils.getDebtsRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}