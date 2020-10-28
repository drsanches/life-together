package ru.drsanches.tests.user_service.user

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONObject
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestGetCurrentUser extends Specification {

    def "success get current user"() {
        given: "registered user and token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def user = RequestUtils.createUser(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "getCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().get(
                path: '/user/current',
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == user
    }

    def "get current user with invalid token"() {
        given: "registered user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.createUser(username, password)
        def token = UUID.randomUUID().toString()

        when: "getCurrentUser is called with invalid token"
        RequestUtils.getUserRestClient().get(
                path: '/user/current',
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}