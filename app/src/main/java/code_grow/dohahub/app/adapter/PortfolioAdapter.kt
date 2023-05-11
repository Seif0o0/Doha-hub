package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.PortfolioItemBinding

class PortfolioAdapter(private val clickListener: OnPortfolioItemClickListener) : ListAdapter<String, PortfolioAdapter.ViewHolder>(PortfolioDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),position,clickListener)
    }

    class ViewHolder private constructor(private val binding: PortfolioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(portfolio: String,position: Int,clickListener: OnPortfolioItemClickListener) {
            binding.portfolio = portfolio
            binding.position = position
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    PortfolioItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


}

open class OnPortfolioItemClickListener(val clickListener:(position:Int)-> Unit){
    fun onImageClicked(position: Int) = clickListener(position)
}
class PortfolioDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}