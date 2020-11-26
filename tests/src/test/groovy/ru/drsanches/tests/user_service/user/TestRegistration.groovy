package ru.drsanches.tests.user_service.user

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestRegistration extends Specification {

    String PATH = "/user/registration"

    def "success user registration"() {
        given: "unique username and password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "registration is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 201
        assert response.getData()["id"] != null
        assert response.getData()["id"] != JSONNull.getInstance()
        assert response.getData()["username"] == username
        assert response.getData()["firstName"] == JSONNull.getInstance()
        assert response.getData()["lastName"] == JSONNull.getInstance()

        and: "correct user was created"
        assert RequestUtils.getUser(username, password) == response.getData()
    }

    def "already existing user registration"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)

        when: "registration is called"
        RequestUtils.getUserRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
        assert e.response.getData()["str"] == "user already exists: $username"
    }
}