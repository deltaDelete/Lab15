package ru.deltadelete.lab15.ui.register_bottom_sheet

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.NotificationCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.textfield.TextInputLayout
import ru.deltadelete.lab15.MainActivity
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.RegisterSheetContentBinding
import ru.deltadelete.lab15.models.UserRegister
import ru.deltadelete.lab15.ui.auth_fragment.NotificationHelper
import ru.deltadelete.lab15.utils.addValidationToList
import java.util.Calendar


class RegisterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: RegisterSheetContentBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterSheetContentBinding.inflate(inflater, container, false)

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
        notificationHelper = NotificationHelper(TAG, R.string.authentification, requireContext())

        setupInputFilters()
        initOnRegisterClick()

        return binding.root
    }

    private fun navigateLoggedIn() {
        findNavController().navigate(
            R.id.SecondFragment
        )
    }

    private val validators = emptyList<Pair<EditText?, TextWatcher?>>().toMutableList()

    private fun setupInputFilters() {
        binding.emailInputLayout.errorIconDrawable = null
        binding.passwordInputLayout.errorIconDrawable = null
        binding.passwordConfirmInputLayout.errorIconDrawable = null

        binding.emailInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
                .validEmail(getString(R.string.invalid_email))
        }

        binding.passwordInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
                .minLength(8, resources.getQuantityString(R.plurals.minimal_length_is, 8, 8))
        }

        binding.passwordConfirmInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
                .minLength(8, resources.getQuantityString(R.plurals.minimal_length_is, 8, 8))
                .textEqualTo(
                    binding.passwordInput.text.toString(),
                    getString(R.string.unmatching_passwords)
                )
        }

        binding.lastnameInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
        }

        binding.firstnameInputLayout.addValidationToList(validators) {
            nonEmpty(getString(R.string.required_field))
        }
    }

    private fun initOnRegisterClick() {
        binding.confirmButton.setOnClickListener {
            binding.confirmButton.icon = progressIndicatorDrawable
            validators.forEach { (editText, textWatcher) ->
                textWatcher?.afterTextChanged(editText?.editableText)
            }
            if (checkErrors()) {
                return@setOnClickListener
            }
            binding.apply {
                this@RegisterBottomSheet.viewModel.register(
                    UserRegister(
                        emailInput.text.toString(),
                        passwordInput.text.toString(),
                        "${lastnameInput.text.toString()} ${firstnameInput.text.toString()}"
                    )
                ) { it, already ->
                    binding.confirmButton.icon = null
                    if (already) {
                        binding.emailInputLayout.error = getString(R.string.already_registered)
                    }
                    if (it == null) {
                        notificationHelper.send(
                            NotificationCompat.Builder(requireContext(), TAG)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("Error")
                                .setContentText("Error then registering")
                                .build()
                        )
                        return@register
                    }

                    this@RegisterBottomSheet.navigateLoggedIn()
                }
            }
        }
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
        const val TAG = "RegisterBottomSheet"
    }
}

