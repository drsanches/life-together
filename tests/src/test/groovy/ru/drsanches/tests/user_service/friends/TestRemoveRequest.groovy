package ru.drsanches.tests.user_service.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestRemoveRequest extends Specification {

    String PATH = "/friends/"

    def "success one side request deletion"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "sendRequest is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        RequestUtils.getIncomingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        RequestUtils.getIncomingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "success two side request deletion"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def user1 = RequestUtils.createUser(username1, password1)
        def user2 = RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "sendRequest is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        RequestUtils.getIncomingRrequests(username1, password1) == JSONArray.fromObject(user2)
        RequestUtils.getOutgoingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        RequestUtils.getIncomingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username2, password2) == JSONArray.fromObject(user1)
        RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "delete nonexistent request"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        RequestUtils.createUser(username2, password2)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "sendRequest is called"
        RequestUtils.getUserRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "the first user has correct relationships"
        RequestUtils.getIncomingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        RequestUtils.getIncomingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    def "delete request for nonexistent user"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def token1 = RequestUtils.getToken(username1, password1)
        def nonexistentUsername = UUID.randomUUID().toString()

        when: "sendRequest is called"
        RequestUtils.getUserRestClient().delete(
                path: PATH + nonexistentUsername,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "delete current user request"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def token1 = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        RequestUtils.getUserRestClient().delete(
                path: PATH + username1,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "delete request with invalid token"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def user1 = RequestUtils.createUser(username1, password1)
        def user2 = RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        def token1 = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        RequestUtils.getUserRestClient().delete(
                path: PATH + username2,
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "the first user has correct relationships"
        RequestUtils.getIncomingRrequests(username1, password1) == new JSONArray()
        RequestUtils.getOutgoingRrequests(username1, password1) == JSONArray.fromObject(user2)
        RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        RequestUtils.getIncomingRrequests(username2, password2) == JSONArray.fromObject(user1)
        RequestUtils.getOutgoingRrequests(username2, password2) == new JSONArray()
        RequestUtils.getFriends(username2, password2) == new JSONArray()
    }
}