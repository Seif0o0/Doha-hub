package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.MyCampaignItemBinding
import code_grow.dohahub.app.model.Campaign

class MyCampaignsAdapter(private val clickListener: OnMyCampaignItemClickListener) :
    ListAdapter<Campaign, MyCampaignsAdapter.ViewHolder>(MyCampaignsDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: MyCampaignItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: Campaign, clickListener: OnMyCampaignItemClickListener) {
            binding.campaign = campaign
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    MyCampaignItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnMyCampaignItemClickListener(val clickListener: (campaign: Campaign) -> Unit) {
    fun onCampaignItemClicked(campaign: Campaign) = clickListener(campaign)
}

class MyCampaignsDiffUtil : DiffUtil.ItemCallback<Campaign>() {
    override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign) =
        oldItem.campaignId == newItem.campaignId

    override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign) = oldItem == newItem
}