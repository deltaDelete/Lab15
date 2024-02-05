package ru.deltadelete.lab15.ui.post_fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import ru.deltadelete.lab15.R
import ru.deltadelete.lab15.databinding.PostItemBinding
import ru.deltadelete.lab15.models.Post
import java.text.SimpleDateFormat
import java.util.Locale

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
            item.creator?.get()?.addOnSuccessListener {
                binding.user.text = it.getString("name")
            }

            if (position <= 0 && position != list.lastIndex) {
                binding.root.shapeAppearanceModel = binding.root.shapeAppearanceModel.toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius16)
                    .setTopRightCorner(CornerFamily.ROUNDED, radius16)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius8)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius8)
                    .build()
            } else if (position == list.lastIndex && position != 0) {
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
//        notifyItemRangeRemoved(0, list.size)
        list.addAll(items)
//        notifyItemRangeInserted(0, items.size)
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