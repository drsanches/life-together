package ru.drsanches.tests

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

class RequestUtils {

    static String SERVER_URL = "http://localhost"
    static String GATEWAY_PORT = "4000"

    // Use for test without gateway
    static String AUTH_PORT = "8084"
    static String USER_PORT = "8085"
    static String DEBTS_PORT = "8086"

    static RESTClient getAuthRestClient() {
        return new RESTClient( "$SERVER_URL:$GATEWAY_PORT")
    }

    static RESTClient getUserRestClient() {
        return new RESTClient( "$SERVER_URL:$GATEWAY_PORT")
    }

    static RESTClient getDebtsRestClient() {
        return new RESTClient( "$SERVER_URL:$GATEWAY_PORT")
    }

    static JSONObject createUser(String username, String password) {
        HttpResponseDecorator response = getUserRestClient().post(
                path: '/user/registration',
                body: [username: username,
                       password: password],
                 requestContentType: ContentType.JSON)
        return response.getData()
    }

    static JSONObject getUser(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "user/current",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getUser(String token) {
        try {
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "user/current",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject updateUser(String username, String password, JSONObject user) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getUserRestClient().put(
                    path: '/user/current',
                    headers: ["Authorization": "Bearer $token"],
                    body:  user,
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void sendFriendRequest(String username, String password, String friendUsername) {
        String token = getToken(username, password)
        getUserRestClient().post(
                path: "/friends/$friendUsername",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)
    }

    static JSONArray getIncomingRrequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "/friends/requests/incoming",
                    headers: ["Authorization": "Bearer $token"],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getOutgoingRrequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "/friends/requests/outgoing",
                    headers: ["Authorization": "Bearer $token"],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getFriends(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "/friends",
                    headers: ["Authorization": "Bearer $token"],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getDebts(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getDebtsRestClient().get(
                    path: "/debts",
                    headers: ["Authorization": "Bearer $token"],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static Date[] sendMoney(String username, String password, List<String> toUserIdList, int money, String message) {
        String token = getToken(username, password)
        Date dateBefore = new Date()
        getDebtsRestClient().post(
                path: "/debts/send/",
                headers: ["Authorization": "Bearer $token"],
                body: [toUserIdList: toUserIdList,
                       money: money,
                       message: message],
                requestContentType: ContentType.JSON)
        Date dateAfter = new Date()
        return [dateBefore, dateAfter]
    }

    static JSONArray getHistory(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getDebtsRestClient().get(
                    path: "/debts/history",
                    headers: ["Authorization": "Bearer $token"],
                    requestContentType : ContentType.JSON)
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void deleteFriend(String username, String password, String friendUsername) {
        String token = getToken(username, password)
        getUserRestClient().delete(
                path: "/friends/$friendUsername",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)
    }

    static void disableUser(String username, String password) {
        String token = getToken(username, password)
        getUserRestClient().delete(
                path: "/user/current",
                headers: ["Authorization": "Bearer $token"],
                requestContentType: ContentType.JSON)
    }

    static String getToken(String username, String password) {
        try {
            String client = "browser:".bytes.encodeBase64().toString()
            HttpResponseDecorator response = getAuthRestClient().post(
                    path: "auth/oauth/token", //Use 'path: /oauth/token' for test without gateway
                    headers: [
                            //TODO: Fix in code
                            "Authorization": "Basic $client"
                    ],
                    body: ["username": username,
                            "password": password,
                            "grant_type": "password",
                            "scope": "ui"],
                    requestContentType : ContentType.URLENC)
            return response.status == 200 ? response.getData()["access_token"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }
}