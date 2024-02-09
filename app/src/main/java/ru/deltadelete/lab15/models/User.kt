package ru.deltadelete.lab15.models

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.util.Date

data class User(
    val email: String,
    val name: String,
    val photoUrl: String,
    val created: Date
) {
    constructor() : this("", "", "", Date())

    fun fromReference(ref: DocumentReference) {
        ref.get().result.toObject<User>()
    }
}

data class UserLogin(
    val login: String,
    val password: String,
)

data class UserRegister(
    val email: String,
    val password: String,
    val name: String
) {
    fun toUser(photoUrl: String, created: Date): User {
        return User(
            email,
            name,
            photoUrl,
            created
        )
    }
}

suspend fun FirebaseUser.toUser(): User? {
    return Firebase.firestore.collection("users").document(this.uid).get().await().let {
        return@let User(
            it.getString("email")!!,
            it.getString("name")!!,
            it.getString("photoUrl")!!,
            it.getTimestamp("created")!!.toDate()
        )
    }
}

suspend fun FirebaseUser.toUserDocument(): DocumentReference {
    return Firebase.firestore.collection("users").document(this.uid).get().await().reference
}