package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.FilterDialogItemBinding
import code_grow.dohahub.app.model.Category

class CategoriesAdapter(private val clickListener: OnCategoryItemClickListener) :
    ListAdapter<Category, CategoriesAdapter.ViewHolder>(CategoriesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            clickListener,
            if (position == currentList.size - 1) View.INVISIBLE else View.VISIBLE
        )
    }

    class ViewHolder private constructor(private val binding: FilterDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            category: Category,
            clickListener: OnCategoryItemClickListener,
            isLastItem: Int
        ) {
            binding.category = category
            binding.clickListener = clickListener
            binding.isLastItem = isLastItem
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    FilterDialogItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

open class OnCategoryItemClickListener(val clickListener: (category: Category) -> Unit) {
    fun onCategoryClicked(category: Category) = clickListener(category)
}

class CategoriesDiffUtil : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category) =
        oldItem.categoryId == newItem.categoryId


    override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
}