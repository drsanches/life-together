package ru.drsanches.tests.user_service.user

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestDisableCurrentUser extends Specification {

    String PATH = "/user/current"

    def "success current user disabling"() {
        given: "registered user and token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "disableCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        assert response.status == 200

        and: "user was disabled"
        assert RequestUtils.getUser(token) == null
        assert RequestUtils.getToken(username, password) == null

        and: "new user with old user credentials has different token"
        assert RequestUtils.createUser(username, password) != null
        assert RequestUtils.getToken(username, password) != token
    }

    def "disable user with invalid token"() {
        given: "registered user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "disableCurrentUser is called with invalid token"
        RequestUtils.getUserRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "user was not deleted"
        assert RequestUtils.getUser(username, password) != null
    }
}