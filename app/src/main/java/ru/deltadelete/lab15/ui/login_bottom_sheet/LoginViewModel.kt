package ru.deltadelete.lab15.ui.login_bottom_sheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import ru.deltadelete.lab15.models.User
import ru.deltadelete.lab15.models.UserLogin
import kotlin.math.log

class LoginViewModel : ViewModel() {

    private val auth = Firebase.auth
    fun login(
        loginBody: UserLogin,
        callback: ((FirebaseUser?) -> Unit)
    ) {
        auth.signInWithEmailAndPassword(loginBody.login, loginBody.password).addOnCompleteListener {
            callback(it.result.user)
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}