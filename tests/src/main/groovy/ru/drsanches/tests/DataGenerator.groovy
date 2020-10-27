package ru.drsanches.tests

class DataGenerator {

    static String createValidUsername() {
        return UUID.randomUUID().toString()
    }

    static String createValidPassword() {
        return UUID.randomUUID().toString()
    }
}