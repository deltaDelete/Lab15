package ru.deltadelete.lab15.ui.new_post_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import ru.deltadelete.lab15.databinding.NewPostDialogContentBinding
import ru.deltadelete.lab15.models.Post
import ru.deltadelete.lab15.ui.post_fragment.PostFragment
import ru.deltadelete.lab15.ui.post_fragment.PostViewModel

class NewPostDialog : BottomSheetDialogFragment() {
    private lateinit var binding: NewPostDialogContentBinding

    private val viewModel: PostViewModel by viewModels()

    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewPostDialogContentBinding.inflate(inflater, container, false)

        val spec = CircularProgressIndicatorSpec(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )

        progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(
            requireContext(), spec
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: СОЗДАВАТЬ ПОСТ
        binding.textInput.doAfterTextChanged {
            viewModel.text.postValue(it.toString())
        }
        viewModel.text.observe(viewLifecycleOwner) {
            binding.textInput.apply {
                if (text.toString() == it) {
                    return@observe
                }
                setText(it)
            }
        }
        binding.createPostButton.setOnClickListener {
            binding.createPostButton.icon = progressIndicatorDrawable
            viewModel.newPost {
                binding.createPostButton.icon = null
                if (it?.isSuccessful == true) {
                    dismiss()
                }
            }
        }
    }
}
