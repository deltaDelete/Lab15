package ru.deltadelete.lab15.ui.register_bottom_sheet

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import ru.deltadelete.lab15.models.User
import ru.deltadelete.lab15.models.UserRegister

class RegisterViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

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
                // TODO ASSOCIATE WITH FIRESTORE
            } catch (e: FirebaseAuthUserCollisionException) {
                callback(null, true)
            } catch (e: Exception) {
                callback(null, false)
            }
        }
    }

    private fun addToDb(registerBody: UserRegister) {
        val fuser = auth.currentUser?.let {
            db.collection("users").document(it.uid)
                .set(registerBody.toUser(it.photoUrl!!, it.metadata!!.creationTimestamp))
        }
    }

    private fun changeName(name: String) {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        )
    }

    companion object {
        const val TAG = "RegisterViewModel"
        const val REMEMBER_ME_KEY = "RememberMe"
    }
}