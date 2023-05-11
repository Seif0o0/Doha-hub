package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ProductItemBinding
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.User

class ProductsAdapter(private val clickListener: OnProductItemClickListener) :
    ListAdapter<Product, ProductsAdapter.ViewHolder>(ProductsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, clickListener: OnProductItemClickListener) {
            binding.product = product
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProductItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnProductItemClickListener(val clickListener: (product: Product) -> Unit) {
    fun onProductClicked(product: Product) = clickListener(product)
}

class ProductsDiffUtil : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product) =
        oldItem.productId == newItem.productId


    override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
}