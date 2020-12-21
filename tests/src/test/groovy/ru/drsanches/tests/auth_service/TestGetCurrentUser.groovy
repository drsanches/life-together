package ru.drsanches.tests.auth_service

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestGetCurrentUser extends Specification {

    String PATH = "auth/current"

    def "successful current user getting"() {
        given: "user with token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "getUser is called"
        HttpResponseDecorator response = RequestUtils.getAuthRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        //TODO: Check body
    }

    def "get current user with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "getUser is called"
        RequestUtils.getAuthRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}