package ru.deltadelete.lab15.ui.controls

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.AttributeSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.UserCardBinding
import ru.deltadelete.lab15.models.User
import java.util.Date
import java.util.Locale

class UserCard : MaterialCardView {
    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private val _userLiveData: MutableLiveData<User> = MutableLiveData<User>()
    private val emptyUser: User = User("anon@example.com", "Anonymous", "", Date())

    var user: User?
        get() = _userLiveData.value
        set(value) {
            if (value == null) {
                _userLiveData.postValue(emptyUser)
                return
            } else {
                _userLiveData.postValue(value as User)
            }
        }

    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private lateinit var binding: UserCardBinding

    private fun initView() {
        inflate(context, R.layout.user_card, this)
        binding = UserCardBinding.bind(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.let {
            _userLiveData.observe(it) { user ->
                binding.nameText.text = user.name
                binding.emailText.text = user.email
                binding.createdText.text = formatter.format(user.created)
                Glide.with(this)
                    .load(user?.photoUrl)
                    .transform(RoundedCorners(100))
                    .fallback(R.drawable.avatar)
                    .into(binding.avatarImage)
            }
        }
    }
}