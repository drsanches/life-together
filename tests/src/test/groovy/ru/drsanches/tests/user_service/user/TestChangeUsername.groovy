package ru.drsanches.tests.user_service.user

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestChangeUsername extends Specification {

    String PATH = "/user/current/changeUsername"

    def "success username change"() {
        given: "registered user, token and new username"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)
        def newUsername = DataGenerator.createValidUsername()

        when: "updateCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [username: newUsername],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user["id"]
        assert response.getData()["username"] == newUsername
        assert response.getData()["firstName"] == JSONNull.instance
        assert response.getData()["lastName"] == JSONNull.instance

        and: "old token is invalid"
        assert RequestUtils.getUser(token) == null

        and: "user was updated"
        assert RequestUtils.getUser(newUsername, password) == response.getData()

        and: "new token is different"
        assert RequestUtils.getToken(newUsername, password) != token
    }

    def "success username change with old username"() {
        given: "registered user, token and invalid new username"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "updateCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [username: username],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "success username change with invalid new username"() {
        given: "registered user, token and invalid new username"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "updateCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [username: newUsername],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        newUsername << ["", null]
    }

    def "username change with invalid token"() {
        given: "registered user, new username and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()
        def newUsername = DataGenerator.createValidUsername()

        when: "updateCurrentUser is called with invalid token"
        RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [username: newUsername],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}