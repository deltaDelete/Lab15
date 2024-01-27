package ru.deltadelete.lab15.ui.register_bottom_sheet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import ru.deltadelete.lab15.models.User
import ru.deltadelete.lab15.models.UserRegister

class RegisterViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val auth = Firebase.auth

    fun register(
        registerBody: UserRegister,
        callback: (FirebaseUser?, alreadyRegistered: Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(registerBody.email, registerBody.password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }
                    changeName(registerBody.name)
                    callback(it.result.user, it.result.additionalUserInfo?.isNewUser == true)
                }
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