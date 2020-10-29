package ru.drsanches.tests.user_service.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestGetFriends extends Specification {
    
    def "success friends getting"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def user2 = RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = RequestUtils.getToken(username1, password1)

        when: "getFriends is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().get(
                path: '/friends',
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == JSONArray.fromObject(user2)
    }

    def "get friends with invalid token"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        RequestUtils.createUser(username2, password2)
        RequestUtils.sendFriendRequest(username1, password1, username2)
        RequestUtils.sendFriendRequest(username2, password2, username1)
        def token1 = UUID.randomUUID().toString()

        when: "getFriends is called with invalid token"
        RequestUtils.getUserRestClient().get(
                path: '/friends',
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}