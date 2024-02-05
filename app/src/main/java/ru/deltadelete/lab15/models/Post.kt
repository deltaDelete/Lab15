package ru.deltadelete.lab15.models

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject

data class Post(
    val text: String,
    val created: Timestamp,
    val creator: DocumentReference?
) {
    constructor() : this("", Timestamp.now(), null)

    constructor(user: DocumentReference) : this("", Timestamp.now(), user)

    constructor(user: DocumentReference, text: String) : this(text, Timestamp.now(), user)

    fun fromReference(ref: DocumentReference) {
        ref.get().result.toObject<Post>()
    }
}
