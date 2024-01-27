package ru.deltadelete.lab15.models

import java.util.Date

data class User(
    val email: String,
    val name: String,
    val photoUrl: String,
    val created: Date
)

data class UserLogin(
    val login: String,
    val password: String,
)

data class UserRegister(
    val email: String,
    val password: String,
    val name: String
)