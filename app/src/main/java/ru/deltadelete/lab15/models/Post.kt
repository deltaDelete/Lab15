package ru.deltadelete.lab15.models

import com.google.firebase.Timestamp

data class Post(
    val text: String,
    val created: Timestamp
) {
    constructor() : this("", Timestamp.now())

    constructor(text: String) : this(text, Timestamp.now())
}
