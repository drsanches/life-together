package ru.drsanches.tests.user_service.user

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestUpdateCurrentUser extends Specification {

    def "success current user update"() {
        given: "registered user, token, new id, username, firstName and lastName"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)
        user["username"] = DataGenerator.createValidUsername()
        user["firstName"] = DataGenerator.createValidFirstName()
        user["lastName"] = DataGenerator.createValidLastName()
        def newId = UUID.randomUUID().toString()

        when: "updateCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: '/user/current',
                headers: ["Authorization": "Bearer $token"],
                body:  [id: newId,
                        username: user["username"],
                        firstName: user["firstName"],
                        lastName: user["lastName"]],
                requestContentType : ContentType.JSON)

        then: "response is correct, id does not change"
        assert response.status == 200
        assert response.getData() == user

        and: "user was updated"
        assert RequestUtils.getUser(user["username"], password) == response.getData()

        and: "old token is invalid"
        assert RequestUtils.getUser(token) == null
        assert RequestUtils.getToken(username, password) == null

        and: "new user with old user credentials has different token"
        assert RequestUtils.createUser(username, password) != null
        assert RequestUtils.getToken(username, password) != token
    }

    def "current user update with invalid token"() {
        given: "registered user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "updateCurrentUser is called with invalid token"
        RequestUtils.getUserRestClient().put(
                path: '/user/current',
                headers: ["Authorization": "Bearer $token"],
                body:  user,
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}