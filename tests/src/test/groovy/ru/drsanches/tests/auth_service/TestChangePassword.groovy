package ru.drsanches.tests.auth_service

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestChangePassword extends Specification {

    String PATH = "/auth/changePassword"

    def "success password change"() {
        given: "registered user, old and new passwords"
        def username = DataGenerator.createValidUsername()
        def oldPassword = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, oldPassword)
        def token = RequestUtils.getToken(username, oldPassword)
        def newPassword = DataGenerator.createValidPassword()

        when: "changePassword is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: oldPassword,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        assert response.status == 200

        and: "old password is invalid"
        assert RequestUtils.getToken(username, oldPassword) == null

        and: "old token is invalid"
        assert RequestUtils.getUser(token) == null

        and: "new token is different"
        assert RequestUtils.getToken(username, newPassword) != token
    }

    def "password change to old password"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def oldPassword = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, oldPassword)
        def token = UUID.randomUUID().toString()

        when: "changePassword is called"
        RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: oldPassword,
                        newPassword: oldPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "password change with wrong old password"() {
        given: "registered user, new password and wrong old password"
        def username = DataGenerator.createValidUsername()
        def oldPassword = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, oldPassword)
        def token = UUID.randomUUID().toString()
        def newPassword = DataGenerator.createValidPassword()
        def wrongOldPassword = DataGenerator.createValidPassword()

        when: "changePassword is called"
        RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: wrongOldPassword,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "password change with invalid token"() {
        given: "registered user and invalid token"
        def username = DataGenerator.createValidUsername()
        def oldPassword = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, oldPassword)
        def token = UUID.randomUUID().toString()
        def newPassword = DataGenerator.createValidPassword()

        when: "changePassword is called with invalid token"
        RequestUtils.getUserRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: oldPassword,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}