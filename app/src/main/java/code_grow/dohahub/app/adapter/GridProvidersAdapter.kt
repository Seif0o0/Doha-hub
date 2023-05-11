package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.GridProviderItemBinding
import code_grow.dohahub.app.model.Provider


class GridProvidersAdapter(private val clickListener: OnGridProviderItemClickListener) :
    ListAdapter<Provider, GridProvidersAdapter.ViewHolder>(GridProvidersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: GridProviderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(provider: Provider, clickListener: OnGridProviderItemClickListener) {
            binding.provider = provider
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    GridProviderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnGridProviderItemClickListener(val clickListener: (user: Provider) -> Unit) {
    fun onProviderClicked(user: Provider) = clickListener(user)
}

class GridProvidersDiffUtil : DiffUtil.ItemCallback<Provider>() {
    override fun areItemsTheSame(oldItem: Provider, newItem: Provider) =
        oldItem.providerId == newItem.providerId


    override fun areContentsTheSame(oldItem: Provider, newItem: Provider) = oldItem == newItem
}