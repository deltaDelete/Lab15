package ru.deltadelete.lab15.ui.post_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.FragmentPostBinding
import ru.deltadelete.lab15.models.Post

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val viewModel: PostViewModel by viewModels(ownerProducer = { this.requireActivity() })
    private val adapter: PostAdapter get() = binding.recyclerview.adapter as PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.recyclerview.adapter = PostAdapter(emptyList<Post>().toMutableList()).apply {
            // TODO Пока не работает
            // TODO Пагинация
            // requestMore = viewModel::requestMore
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start()
        binding.newPostButton.setOnClickListener {
            try {
                val findNavController = findNavController()
                findNavController.navigate(R.id.newPostDialog)
            } catch (e: Exception) {
                Log.d(TAG, "Exception while opening NewPostDialog", e)
            }
        }

        lifecycleScope.launch {
            viewModel.events.collectLatest {
                when (it) {
                    PostViewModel.Event.Initial -> {}
                    is PostViewModel.Event.NewItems -> {
                        if (it.clear) {
                            adapter.replaceAll(it.items)
                            return@collectLatest
                        }
                        adapter.appendStartAll(it.items)
                    }

                    is PostViewModel.Event.NewItemAtStart -> {
                        adapter.appendStart(it.item)
                        binding.recyclerview.scrollToPosition(0)
                    }

                    is PostViewModel.Event.MoreItems -> {
                        adapter.addAll(it.items)
                    }

                    is PostViewModel.Event.Error -> {
                        Snackbar.make(binding.root, it.messageId, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Paused")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Resume")
    }

    companion object {
        const val TAG = "PostFragment"
    }
}

