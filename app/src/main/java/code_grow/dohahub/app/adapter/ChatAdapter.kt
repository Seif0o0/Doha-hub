package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ReceiverMessageItemBinding
import code_grow.dohahub.app.databinding.SenderMessageItemBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Message

private const val MESSAGE_TYPE_SENDER = 1
private const val MESSAGE_TYPE_RECEIVER = 2

class ChatAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessagesDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).senderId == UserInfo.userId)
            return MESSAGE_TYPE_SENDER
        return MESSAGE_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MESSAGE_TYPE_SENDER)
            return SenderViewHolder.from(parent)

        return ReceiverViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SenderViewHolder)
            holder.bind(getItem(position))
        else
            (holder as ReceiverViewHolder).bind(getItem(position))
    }

    class ReceiverViewHolder private constructor(private val binding: ReceiverMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: Message
        ) {
            binding.chatMessage = message
        }

        companion object {
            fun from(parent: ViewGroup): ReceiverViewHolder {
                return ReceiverViewHolder(
                    ReceiverMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class SenderViewHolder private constructor(private val binding: SenderMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            message: Message
        ) {
            binding.chatMessage = message
        }

        companion object {
            fun from(parent: ViewGroup): SenderViewHolder {
                return SenderViewHolder(
                    SenderMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

class MessagesDiffUtil : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.senderId == newItem.senderId


    override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
}