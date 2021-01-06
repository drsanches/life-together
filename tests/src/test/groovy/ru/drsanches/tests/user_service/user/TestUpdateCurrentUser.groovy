package ru.drsanches.tests.user_service.user

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestUpdateCurrentUser extends Specification {

    String PATH = "/user/current"

    def "success current user update"() {
        given: "registered user, token, firstName and lastName"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)
        user["firstName"] = DataGenerator.createValidFirstName()
        user["lastName"] = DataGenerator.createValidLastName()

        when: "updateCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [firstName: user["firstName"],
                        lastName: user["lastName"]],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == user

        and: "user was updated"
        assert RequestUtils.getUser(username, password) == response.getData()
    }

    def "current user update with invalid token"() {
        given: "registered user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "updateCurrentUser is called with invalid token"
        RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  user,
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}