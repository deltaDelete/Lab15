package ru.deltadelete.lab15.ui.post_fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.android.gms.tasks.Task
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.FragmentPostBinding
import ru.deltadelete.lab15.databinding.PostItemBinding
import ru.deltadelete.lab15.models.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.recyclerview.adapter = PostAdapter(emptyList<Post>().toMutableList())
        viewModel.posts.observe(viewLifecycleOwner) {
            (binding.recyclerview.adapter as PostAdapter?)?.replaceAll(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_newPostDialog)
        }
    }
}

class PostViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val mapper = JsonMapper.builder()
        .build()

    val posts = MutableLiveData<List<Post>>()
    val text = MutableLiveData<String>("")

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
                this.posts.value = posts
            }
    }

    fun newPost(callback: (Task<DocumentReference>?) -> Unit) {
        if (text.value == null) {
            callback(null)
            return
        }
        val post = Post(text.value.toString())
        db.collection("posts")
            .add(post)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // TODO: rework new item events to StateFlow
                    posts.value = posts.value?.toMutableList()?.apply {
                        this.add(0, post)
                    }
                }
                callback(it)
            }
    }
}

class PostAdapter(
    private val list: MutableList<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    inner class ViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val radius16 =
            binding.root.context.resources.getDimension(R.dimen.corner_radius_last_first)
        private val radius8 =
            binding.root.context.resources.getDimension(R.dimen.corner_radius_middle)

        fun bind(item: Post, position: Int) {
            binding.text.text = item.text
            binding.created.text = format.format(item.created.toDate())

            if (position <= 0 && position != list.lastIndex) {
                binding.root.shapeAppearanceModel = binding.root.shapeAppearanceModel.toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius16)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius16)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius8)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius8)
                    .build()
            }
            else if (position == list.lastIndex && position != 0) {
                binding.root.shapeAppearanceModel = binding.root.shapeAppearanceModel.toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius8)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius8)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius16)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius16)
                    .build()
            } else if (position <= 0 && position == list.lastIndex) {
                binding.root.shapeAppearanceModel = binding.root.shapeAppearanceModel.toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius16)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius16)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius16)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius16)
                    .build()
                binding.root.invalidate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }


    @Synchronized
    fun add(item: Post) {
        list.add(item)
        notifyItemInserted(list.lastIndex)
    }


    @Synchronized
    fun remove(item: Post) {
        val position = list.indexOf(item)
        if (list.remove(item)) {
            notifyItemRemoved(position)
        }
    }

    @Synchronized
    fun addAll(vararg items: Post) {
        val before = list.lastIndex
        list.addAll(items)
        notifyItemRangeInserted(before, list.lastIndex)
    }

    @Synchronized
    fun addAll(items: List<Post>) {
        val before = list.lastIndex
        list.addAll(items)
        notifyItemRangeInserted(before, list.lastIndex)
    }

    @Synchronized
    fun removeAll(vararg items: Post) {
        list.removeAll(items.toSet())
    }

    @SuppressLint("notifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    @Synchronized
    @SuppressLint("notifyDataSetChanged")
    fun replaceAll(items: List<Post>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    val size: Int
        get() = list.size
}