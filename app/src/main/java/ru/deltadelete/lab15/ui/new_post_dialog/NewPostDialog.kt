package ru.deltadelete.lab15.ui.new_post_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.deltadelete.lab15.databinding.NewPostDialogContentBinding
import ru.deltadelete.lab15.ui.post_fragment.PostViewModel

class NewPostDialog : BottomSheetDialogFragment() {
    private lateinit var binding: NewPostDialogContentBinding

    private val viewModel: PostViewModel by viewModels(ownerProducer = { this.requireActivity() })

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
        binding.textInput.doAfterTextChanged {
            viewModel.text.postValue(it.toString())
        }
        val context = requireContext()
        val markwon = Markwon.builder(context)
            .usePlugin(GlideImagesPlugin.create(context))
            .build()
        val editor = MarkwonEditor.create(markwon)
        binding.textInput.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        viewModel.text.observe(viewLifecycleOwner) {
            binding.textInput.apply {
                if (text.toString() == it) {
                    return@observe
                }
                setText(it)
            }
        }

        lifecycleScope.launch {
            viewModel.events.collectLatest {
                if (it is PostViewModel.Event.Error) {
                    binding.createPostButton.icon = null
                    binding.textInputLayout.error = getString(it.messageId)
                    return@collectLatest
                }
                if (it !is PostViewModel.Event.NewItemAtStart) {
                    return@collectLatest
                }
                if (it.item.text == viewModel.text.value) {
                    binding.createPostButton.icon = null
                    viewModel.dialogDismissing()
                    dismiss()
                }
            }
        }

        binding.createPostButton.setOnClickListener {
            binding.createPostButton.icon = progressIndicatorDrawable
            viewModel.newPost()
        }
    }
}
