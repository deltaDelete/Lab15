package ru.deltadelete.lab15.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject

data class Post(
    @DocumentId
    val id: String,
    val text: String,
    val created: Timestamp,
    val creator: DocumentReference?
) {
    constructor() : this("", "", Timestamp.now(), null)

    constructor(user: DocumentReference) : this("", "", Timestamp.now(), user)

    constructor(user: DocumentReference, text: String) : this("", text, Timestamp.now(), user)
    constructor(id: String, user: DocumentReference, text: String) : this(id, text, Timestamp.now(), user)

    fun fromReference(ref: DocumentReference) {
        ref.get().result.toObject<Post>()
    }
}
