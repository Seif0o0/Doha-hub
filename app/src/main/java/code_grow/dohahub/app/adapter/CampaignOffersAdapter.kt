package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.CampaignOfferItemBinding
import code_grow.dohahub.app.model.CampaignOffer

class CampaignOffersAdapter(private val clickListener:OnOfferItemClickListener):
ListAdapter<CampaignOffer,CampaignOffersAdapter.ViewHolder>(CampaignOffersDiffUtil()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding:CampaignOfferItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(offer:CampaignOffer , clickListener: OnOfferItemClickListener){
            binding.offer = offer
            binding.clickListener = clickListener
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                return ViewHolder(
                    CampaignOfferItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnOfferItemClickListener(val clickListener:(offer:CampaignOffer,isShowingDetails:Boolean)->Unit){
    fun onOfferItemClicked(offer: CampaignOffer,isShowingDetails:Boolean) = clickListener(offer,isShowingDetails)
}

class CampaignOffersDiffUtil:DiffUtil.ItemCallback<CampaignOffer>(){
    override fun areItemsTheSame(oldItem: CampaignOffer, newItem: CampaignOffer) = oldItem.campaignId == newItem.campaignId

    override fun areContentsTheSame(oldItem: CampaignOffer, newItem: CampaignOffer) = oldItem == newItem
}
