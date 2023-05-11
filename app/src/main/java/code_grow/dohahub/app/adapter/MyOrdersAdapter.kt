package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.MyOrderItemBinding
import code_grow.dohahub.app.model.OrderResponse

class MyOrdersAdapter(private val clickListener: OnMyOrderItemClickListener) :
    ListAdapter<OrderResponse, MyOrdersAdapter.ViewHolder>(MyOrdersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MyOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderResponse, clickListener: OnMyOrderItemClickListener) {
            binding.order = order
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyOrderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnMyOrderItemClickListener(val clickListener: (order: OrderResponse) -> Unit) {
    fun onOrderItemClicked(order: OrderResponse) = clickListener(order)
}

class MyOrdersDiffUtil : DiffUtil.ItemCallback<OrderResponse>() {
    override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
        oldItem.orderDetails.orderId == newItem.orderDetails.orderId


    override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
        oldItem == newItem
}