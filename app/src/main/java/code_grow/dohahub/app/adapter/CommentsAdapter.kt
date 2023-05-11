package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.CommentItemBinding
import code_grow.dohahub.app.model.Comment

class CommentsAdapter : ListAdapter<Comment, CommentsAdapter.ViewHolder>(CommentsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            if (position == currentList.size - 1) View.GONE else View.VISIBLE
        )
    }

    class ViewHolder private constructor(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            comment: Comment,
            visibility: Int
        ) {
            binding.commentObj = comment
            binding.lineVisibility = visibility
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    CommentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

class CommentsDiffUtil : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
        oldItem.commentId == newItem.commentId


    override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
}