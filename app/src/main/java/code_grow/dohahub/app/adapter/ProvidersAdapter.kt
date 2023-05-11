package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ProviderItemBinding
import code_grow.dohahub.app.model.Provider

class ProvidersAdapter(private val clickListener: OnProviderItemClickListener) :
    ListAdapter<Provider, ProvidersAdapter.ViewHolder>(ProvidersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ProviderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(provider: Provider, clickListener: OnProviderItemClickListener) {
            binding.provider = provider
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProviderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnProviderItemClickListener(val clickListener: (user: Provider) -> Unit) {
    fun onProviderClicked(user: Provider) = clickListener(user)
}

class ProvidersDiffUtil : DiffUtil.ItemCallback<Provider>() {
    override fun areItemsTheSame(oldItem: Provider, newItem: Provider) =
        oldItem.providerId == newItem.providerId


    override fun areContentsTheSame(oldItem: Provider, newItem: Provider) = oldItem == newItem
}