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

    static JSONObject getUser(String username, String password) {
        try {
            String token = getToken(username, password)
            HttpResponseDecorator response = getUserRestClient().get(
                    path: "user/current",
                    headers: ["Authorization": "Bearer $token"])
            return response.status == 200 ? response.getData() : null
        } catch(Exception e) {
            return null
        }
    }

    static String getToken(String username, String password) {
        HttpResponseDecorator response = getAuthRestClient().post(
                path: "oauth/token",
                headers: [
                        //TODO: Fix in code
                        "Authorization": "Basic YnJvd3Nlcjo=",
                        "Content-Type": "application/x-www-form-urlencoded"
                ],
                body: ["username": username,
                        "password": password,
                        "grant_type": "password",
                        "scope": "ui"],
                requestContentType : ContentType.URLENC)
        return response.getData()["access_token"]
    }
}