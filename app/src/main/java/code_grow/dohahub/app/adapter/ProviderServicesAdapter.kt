package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ProviderServiceItemBinding
import code_grow.dohahub.app.model.ProviderService

class ProviderServicesAdapter(private val clickListener: OnProviderServiceItemClickListener) :
    ListAdapter<ProviderService, ProviderServicesAdapter.ViewHolder>(ProviderServicesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position, clickListener)
    }

    class ViewHolder private constructor(private val binding: ProviderServiceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            service: ProviderService,
            position: Int,
            clickListener: OnProviderServiceItemClickListener
        ) {
            binding.service = service
            binding.position = position
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ProviderServiceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}


open class OnProviderServiceItemClickListener(val clickListener: (service: ProviderService, position: Int) -> Unit) {
    fun onProviderServiceClicked(service: ProviderService, position: Int) =
        clickListener(service, position)
}

class ProviderServicesDiffUtil : DiffUtil.ItemCallback<ProviderService>() {
    override fun areItemsTheSame(oldItem: ProviderService, newItem: ProviderService) =
        oldItem.serviceId == newItem.serviceId


    override fun areContentsTheSame(oldItem: ProviderService, newItem: ProviderService) =
        oldItem == newItem
}