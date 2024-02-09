package ru.deltadelete.lab15.ui.register_bottom_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.deltadelete.lab15.models.UserRegister
import java.util.Date

class RegisterViewModel : ViewModel() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun register(
        registerBody: UserRegister,
        callback: (FirebaseUser?, alreadyRegistered: Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(registerBody.email, registerBody.password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            callback(it.result?.user, true)
                            return@addOnCompleteListener
                        }
                        changeName(registerBody.name)
                        addToDb(registerBody)
                        callback(it.result.user, it.result.additionalUserInfo?.isNewUser == true)
                    }
            } catch (e: FirebaseAuthUserCollisionException) {
                callback(null, true)
            } catch (e: Exception) {
                callback(null, false)
            }
        }
    }

    private fun addToDb(registerBody: UserRegister) {
        auth.currentUser?.let {
            db.collection("users").document(it.uid)
                .set(registerBody.toUser(it.photoUrl.toString(), Date(it.metadata!!.creationTimestamp)))
        }
    }

    private fun changeName(name: String) {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        )
    }
}