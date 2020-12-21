package ru.drsanches.tests.auth_service

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestLogin extends Specification {

    String PATH = "auth/oauth/token" //Use "/oauth/token" for testing without gateway

    def "successful login"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def client = "browser:".bytes.encodeBase64().toString()

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getAuthRestClient().post(
                path: PATH,
                headers: ["Authorization": "Basic $client"],
                body: ["username": username,
                       "password": password,
                       "grant_type": "password",
                       "scope": "ui"],
                requestContentType : ContentType.URLENC)

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.getData()["access_token"]
        assert RequestUtils.getUser(token as String) != null
    }

    def "login with invalid password"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def client = "browser:".bytes.encodeBase64().toString()
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        HttpResponseDecorator response = RequestUtils.getAuthRestClient().post(
                path: PATH,
                headers: ["Authorization": "Basic $client"],
                body: ["username": username,
                       "password": invalidPassword,
                       "grant_type": "password",
                       "scope": "ui"],
                requestContentType : ContentType.URLENC)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        //TODO: Why?
        assert e.response.status == 400
    }
}