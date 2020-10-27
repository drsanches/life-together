package ru.drsanches.tests

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import net.sf.json.JSONNull
import spock.lang.Specification

class Test extends Specification {

    def "success registration"() {
        given: "unique username and password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "registration is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().post(
                path: '/user/registration',
                body:  [username: username,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        //TODO: 201
        assert response.status == 200                                           : "Wrong response status"
        assert response.getData()["id"] != null                                 : "Response does not contain 'id' field"
        assert response.getData()["id"] != JSONNull.getInstance()               : "Response contains null 'id'"
        assert response.getData()["username"] == username                       : "Invalid response username"
        assert response.getData()["firstName"] == JSONNull.getInstance()        : "Invalid response firsName"
        assert response.getData()["lastName"] == JSONNull.getInstance()         : "Invalid response lastName"

        and: "correct user was created"
        assert RequestUtils.getUser(username, password) == response.getData()   : "Correct user was not created"
    }
}