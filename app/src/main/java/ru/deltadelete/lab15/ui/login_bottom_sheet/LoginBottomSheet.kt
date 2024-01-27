package ru.deltadelete.lab15.ui.login_bottom_sheet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.deltadelete.lab15.MainActivity
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.SecondFragment
import ru.deltadelete.lab15.databinding.LoginSheetContentBinding
import ru.deltadelete.lab15.models.User
import ru.deltadelete.lab15.models.UserLogin
import ru.deltadelete.lab15.utils.addValidationToList

class LoginBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: LoginSheetContentBinding

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginSheetContentBinding.inflate(inflater, container, false)

        (dialog as BottomSheetDialog).behavior.apply {
            skipCollapsed = true
        }

        val spec = CircularProgressIndicatorSpec(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )

        progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(
            requireContext(), spec
        )

        setupInputFilters()
        initOnLoginClick()

        return binding.root
    }

    private val validators = emptyList<Pair<EditText?, TextWatcher?>>().toMutableList()
    private fun setupInputFilters() {
        binding.loginInputLayout.errorIconDrawable = null
        binding.passwordInputLayout.errorIconDrawable = null

        binding.loginInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
                .validEmail(getString(R.string.invalid_email))
        }

        binding.passwordInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
                .minLength(8, resources.getQuantityString(R.plurals.minimal_length_is, 8, 8))
        }
    }

    private fun initOnLoginClick() {
        binding.confirmButton.setOnClickListener {
            binding.confirmButton.icon = progressIndicatorDrawable
            validators.forEach { (editText, textWatcher) ->
                textWatcher?.afterTextChanged(editText?.editableText)
            }
            if (checkErrors()) {
                binding.confirmButton.icon = null
                return@setOnClickListener
            }
            binding.apply {
                this@LoginBottomSheet.viewModel.login(
                    UserLogin(
                        loginInput.text.toString(),
                        passwordInput.text.toString()
                    )
                ) {
                    binding.confirmButton.icon = null
                    if (it == null) {
                        return@login
                    }
                    navigateLoggedIn()
                }
            }
        }
    }

    private fun navigateLoggedIn() {
        findNavController().navigate(
            R.id.SecondFragment
        )
    }

    private fun checkErrors(): Boolean {
        binding.root.forEach {
            if (it is TextInputLayout) {
                if (it.error != null) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val TAG = "LoginBottomSheet"
    }
}

