package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.MyServiceItemBinding
import code_grow.dohahub.app.model.MyService
import code_grow.dohahub.app.model.Provider

class MyServicesAdapter(private val clickListener: OnMyServiceItemClickListener) :
    ListAdapter<MyService, MyServicesAdapter.ViewHolder>(MyServicesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MyServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            provider: MyService,
            clickListener: OnMyServiceItemClickListener
        ) {
            binding.provider = provider
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyServiceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnMyServiceItemClickListener(val clickListener: (provider: MyService) -> Unit) {
    fun onMyServiceClicked(provider: MyService) =
        clickListener(provider)
}

class MyServicesDiffUtil : DiffUtil.ItemCallback<MyService>() {
    override fun areItemsTheSame(oldItem: MyService, newItem: MyService) =
        oldItem.serviceId == newItem.serviceId


    override fun areContentsTheSame(oldItem: MyService, newItem: MyService) =
        oldItem == newItem
}