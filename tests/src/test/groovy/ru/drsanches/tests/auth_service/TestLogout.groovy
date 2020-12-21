package ru.drsanches.tests.auth_service

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestLogout extends Specification {

    String PATH = "auth/log_out"

    def "successful logout"() {
        given: "user after login"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "logout is called"
        HttpResponseDecorator response = RequestUtils.getAuthRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        assert response.status == 200

        and: "new token is correct"
        def newToken = RequestUtils.getToken(username, password)
        assert RequestUtils.getUser(newToken as String) != null

        and: "new token is different"
        assert newToken != token

        and: "old token is invalid"
        assert RequestUtils.getUser(token as String) == null
    }

    def "logout with invalid token"() {
        given: "user after login"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "logout is called"
        HttpResponseDecorator response = RequestUtils.getAuthRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}