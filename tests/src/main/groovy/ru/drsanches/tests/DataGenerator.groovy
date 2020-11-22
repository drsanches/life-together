package ru.drsanches.tests

class DataGenerator {

    static String createValidUsername() {
        return UUID.randomUUID().toString()
    }

    static String createValidPassword() {
        return UUID.randomUUID().toString()
    }

    static String createValidFirstName() {
        return UUID.randomUUID().toString()
    }

    static String createValidLastName() {
        return UUID.randomUUID().toString()
    }

    static Integer createValidMoney() {
        return Math.abs(new Random().nextInt())
    }

    static String createValidMessage() {
        return UUID.randomUUID().toString()
    }
}