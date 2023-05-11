package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ProductCategoryItemBinding
import code_grow.dohahub.app.model.ProductCategory

class ProductCategoriesAdapter(private val clickListener: OnProductCategoryItemClickListener) :
    ListAdapter<ProductCategory, ProductCategoriesAdapter.ViewHolder>(
        ProductCategoriesDiffUtil()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ProductCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: ProductCategory, clickListener: OnProductCategoryItemClickListener) {
            binding.category = category
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProductCategoryItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

}

open class OnProductCategoryItemClickListener(val clickListener: (product: ProductCategory) -> Unit) {
    fun onProductCategoryClicked(product: ProductCategory) = clickListener(product)
}

class ProductCategoriesDiffUtil : DiffUtil.ItemCallback<ProductCategory>() {
    override fun areItemsTheSame(oldItem: ProductCategory, newItem: ProductCategory) =
        oldItem.categoryId == newItem.categoryId


    override fun areContentsTheSame(oldItem: ProductCategory, newItem: ProductCategory) =
        oldItem == newItem
}