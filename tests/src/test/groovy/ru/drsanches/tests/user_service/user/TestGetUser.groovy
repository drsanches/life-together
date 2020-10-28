package ru.drsanches.tests.user_service.user

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestGetUser extends Specification {

    def "success get user"() {
        given: "registered user with token and another registered user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def token1 = RequestUtils.getToken(username1, password1)
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def user2 = RequestUtils.createUser(username2, password2)
        user2["firstName"] = DataGenerator.createValidFirstName()
        user2["lastName"] = DataGenerator.createValidLastName()
        RequestUtils.updateUser(username2, password2, user2)

        when: "getUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().get(
                path: "/user/$username2",
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == user2
    }

    def "get user with invalid token"() {
        given: "two registered users and invalid token"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username1, password1)
        def token1 = UUID.randomUUID().toString()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.createUser(username2, password2)

        when: "getUser is called with invalid token"
        RequestUtils.getUserRestClient().get(
                path: "/user/$username2",
                headers: ["Authorization": "Bearer $token1"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}