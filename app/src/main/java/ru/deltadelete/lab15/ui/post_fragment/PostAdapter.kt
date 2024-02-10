package ru.deltadelete.lab15.ui.post_fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.PostItemBinding
import ru.deltadelete.lab15.models.Post
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val list: MutableList<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    var requestMore: ((Post, Int) -> Unit)? = null
    var longItemClickListener: ((Post) -> Unit)? = null

    inner class ViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val markwon = Markwon.builder(binding.root.context)
            .usePlugin(GlideImagesPlugin.create(binding.root.context))
            .usePlugin(JLatexMathPlugin.create(binding.text.textSize))
            .build()

        private lateinit var item: Post

        fun bind(item: Post, position: Int) {
            binding.root.setOnLongClickListener {
                longItemClickListener?.invoke(item)
                return@setOnLongClickListener true
            }
            markwon.setMarkdown(binding.text, item.text)
            binding.created.text = format.format(item.created.toDate())
            item.creator?.get()?.addOnSuccessListener {
                binding.user.text = it.getString("name")
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
        val item = list[position]
        holder.bind(item, position)
        if (position == list.lastIndex) {
            requestMore?.invoke(list[list.lastIndex], list.lastIndex)
        }
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
        notifyItemRangeInserted(before, items.size)
    }

    @Synchronized
    fun addAll(items: List<Post>) {
        val before = list.size
        list.addAll(items)
        notifyItemRangeInserted(before, items.size)
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

    @Synchronized
    fun appendStart(item: Post) {
        list.add(0, item)
        notifyItemInserted(0)
    }

    @Synchronized
    fun appendStartAll(items: List<Post>) {
        list.addAll(0, items)
        notifyItemRangeInserted(0, items.size)
    }

    val size: Int
        get() = list.size
}