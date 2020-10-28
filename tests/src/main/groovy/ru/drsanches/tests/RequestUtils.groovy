package ru.drsanches.tests

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONObject

class RequestUtils {

    static String SERVER_URL = "http://localhost"
    static String AUTH_PORT = "8084"
    static String USER_PORT = "8085"

    static RESTClient getAuthRestClient() {
        return new RESTClient( "$SERVER_URL:$AUTH_PORT")
    }

    static RESTClient getUserRestClient() {
        return new RESTClient( "$SERVER_URL:$USER_PORT")
    }

    static JSONObject createUser(String username, String password) {
        HttpResponseDecorator response = getUserRestClient().post(
                path: '/user/registration',
                body:  [username: username,
                        password: password],
                requestContentType : ContentType.JSON)
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
            return null
        }
    }

    static String getToken(String username, String password) {
        try {
            HttpResponseDecorator response = getAuthRestClient().post(
                    path: "oauth/token",
                    headers: [
                            //TODO: Fix in code
                            "Authorization": "Basic YnJvd3Nlcjo="
                    ],
                    body: ["username": username,
                            "password": password,
                            "grant_type": "password",
                            "scope": "ui"],
                    requestContentType : ContentType.URLENC)
            return response.status == 200 ? response.getData()["access_token"] : null
        } catch (Exception e) {
            return null
        }
    }
}