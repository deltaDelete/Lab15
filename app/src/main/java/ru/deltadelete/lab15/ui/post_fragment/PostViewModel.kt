package ru.deltadelete.lab15.ui.post_fragment

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.models.Post
import ru.deltadelete.lab15.models.toUserDocument

class PostViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val eventFlow = MutableStateFlow<Event>(Event.Initial)

    sealed class Event {
        object Initial : Event()

        data class NewItems(val items: List<Post>, val clear: Boolean) : Event()

        data class NewItemAtStart(val item: Post) : Event()

        data class Error(@StringRes val messageId: Int) : Event()
    }

    val text = MutableLiveData<String>("")

    val events: StateFlow<Event> get() = eventFlow.asStateFlow()

    init {
        db.collection("posts")
            .orderBy("created", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                if (it.result.isEmpty) {
                    return@addOnCompleteListener
                }
                val posts = it.result.documents.mapNotNull { doc ->
                    doc.toObject<Post>()
                }
                viewModelScope.launch {
                    eventFlow.emit(Event.NewItems(posts, true))
                }
            }
        db.collection("posts").addSnapshotListener { value, error ->
            Log.d("$TAG.SnapshotListener", value.toString())
        }
    }

    fun newPost() {
        if (text.value.isNullOrBlank()) {
            viewModelScope.launch {
                eventFlow.emit(Event.Error(R.string.error_new_post_empty_text))
            }
            return
        }
        viewModelScope.launch {
            val user = auth.currentUser?.toUserDocument()
            if (user == null) {
                emitErrorEvent(R.string.error_new_post_unknown)
                return@launch
            }
            val post = Post(user, text.value.toString())
            db.collection("posts")
                .add(post)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        retrieveFreshlyAddedPost(task.result.id)
                        return@addOnCompleteListener
                    }
                    emitErrorEvent(R.string.error_new_post_unknown)
                }
        }
    }

    private fun retrieveFreshlyAddedPost(id: String) {
        db.collection("posts")
            .document(id)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val postInserted = it.result.toObject<Post>() ?: return@addOnCompleteListener
                    emitNewItemAtStart(postInserted)
                } else {
                    emitErrorEvent(R.string.error_new_post_unknown)
                }
            }
    }

    private fun emitErrorEvent(@StringRes id: Int) {
        viewModelScope.launch {
            eventFlow.emit(Event.Error(id))
        }
    }

    private fun emitNewItemAtStart(item: Post) {
        viewModelScope.launch {
            eventFlow.emit(Event.NewItemAtStart(item))
        }
    }

    companion object {
        const val TAG: String = "PostViewModel"
    }
}