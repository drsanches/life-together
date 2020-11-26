package ru.drsanches.tests.user_service.user

import groovyx.net.http.HttpResponseDecorator
import net.sf.json.JSONArray
import ru.drsanches.tests.DataGenerator
import ru.drsanches.tests.RequestUtils
import spock.lang.Specification

class TestFriendUserDisable extends Specification {

    String PATH = "/user/current"

    def "success friend user disabling"() {
        given: "4 users"
        def disablingUsername = DataGenerator.createValidUsername()
        def disablingPassword = DataGenerator.createValidPassword()
        def incomingUsername = DataGenerator.createValidUsername()
        def incomingPassword = DataGenerator.createValidPassword()
        def outgoingUsername = DataGenerator.createValidUsername()
        def outgoingPassword = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        RequestUtils.createUser(disablingUsername, disablingPassword)
        RequestUtils.createUser(incomingUsername, incomingPassword)
        RequestUtils.createUser(outgoingUsername, outgoingPassword)
        RequestUtils.createUser(friendUsername, friendPassword)
        def token = RequestUtils.getToken(disablingUsername, disablingPassword)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, disablingUsername)
        RequestUtils.sendFriendRequest(disablingUsername, disablingPassword, outgoingUsername)
        RequestUtils.sendFriendRequest(disablingUsername, disablingPassword, friendUsername)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, disablingUsername)

        when: "disableCurrentUser is called"
        HttpResponseDecorator response = RequestUtils.getUserRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        assert response.status == 200

        and: "user was disabled"
        assert RequestUtils.getUser(token) == null
        assert RequestUtils.getToken(disablingUsername, disablingPassword) == null

        and: "users have correct relationships"
        RequestUtils.getIncomingRrequests(incomingUsername, incomingPassword) == new JSONArray()
        RequestUtils.getOutgoingRrequests(incomingUsername, incomingPassword) == new JSONArray()
        RequestUtils.getFriends(incomingUsername, incomingPassword) == new JSONArray()
        RequestUtils.getIncomingRrequests(outgoingUsername, outgoingPassword) == new JSONArray()
        RequestUtils.getOutgoingRrequests(outgoingUsername, outgoingPassword) == new JSONArray()
        RequestUtils.getFriends(outgoingUsername, outgoingPassword) == new JSONArray()
        RequestUtils.getIncomingRrequests(friendUsername, friendPassword) == new JSONArray()
        RequestUtils.getOutgoingRrequests(friendUsername, friendPassword) == new JSONArray()
        RequestUtils.getFriends(friendUsername, friendPassword) == new JSONArray()
    }
}