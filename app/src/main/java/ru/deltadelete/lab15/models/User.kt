package ru.deltadelete.lab15.models

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

data class User(
    val email: String,
    val name: String,
    val photoUrl: Uri,
    val created: Long
) {
    constructor() : this("", "", Uri.EMPTY, 0)

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
    fun toUser(photoUrl: Uri, created: Long): User {
        return User(
            email,
            name,
            photoUrl,
            created
        )
    }
}

fun FirebaseUser.toUser(): User? {
    return Firebase.firestore.collection("users").document(this.uid).get().result.toObject<User>()
}

suspend fun FirebaseUser.toUserDocument(): DocumentReference {
    return Firebase.firestore.collection("users").document(this.uid).get().await().reference
}