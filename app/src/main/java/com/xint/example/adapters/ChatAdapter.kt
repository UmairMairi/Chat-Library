package com.xint.example.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xint.chatlibrary.models.ChatMessageModel
import com.xint.example.R
import com.xint.example.databinding.FriendChatItemBinding
import com.xint.example.databinding.MyChatItemBinding
import com.xint.example.listeners.ChatItemClickListener
import com.xint.example.utils.Constants


class ChatAdapter(var list: ArrayList<ChatMessageModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recentUserId: String = "-1"
    private var listener: ChatItemClickListener? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Constants.ChatViewType.TEXT_MESSAGE -> {
                TextMessageViewHolder(
                    FriendChatItemBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            else -> {
                TextMessageSelfViewHolder(
                    MyChatItemBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatModel = list[position]
        recentUserId = chatModel.user_id

        when (chatModel.viewType) {
            Constants.ChatViewType.TEXT_MESSAGE_SELF -> {
                val msgViewHolder = holder as TextMessageSelfViewHolder
                msgViewHolder.binding.tvContent.text = chatModel.message_model!!.messageString
                msgViewHolder.binding.tvDateTime.text = chatModel.message_date
                msgViewHolder.binding.tvDateTime.visibility = View.VISIBLE
                when (chatModel.messageStatus) {
                    "0" -> {
                        msgViewHolder.binding.imgCheck.visibility = View.VISIBLE
                        msgViewHolder.binding.imgCheck.setImageResource(R.drawable.icon_clock)
                    }
                    "1" -> {
                        msgViewHolder.binding.imgCheck.visibility = View.VISIBLE
                        msgViewHolder.binding.imgCheck.setImageResource(R.drawable.icon_tick)
                    }
                    "2" -> {
                        msgViewHolder.binding.imgCheck.visibility = View.VISIBLE
                        msgViewHolder.binding.imgCheck.setImageResource(R.drawable.icon_tick)
                    }
                    "3" -> {
                        msgViewHolder.binding.imgCheck.visibility = View.VISIBLE
                        msgViewHolder.binding.imgCheck.setImageResource(R.drawable.icon_double_tick_grey)
                    }
                    "4" -> {
                        msgViewHolder.binding.imgCheck.visibility = View.VISIBLE
                        msgViewHolder.binding.imgCheck.setImageResource(R.drawable.icon_double_tick)
                    }
                }

                msgViewHolder.itemView.setOnLongClickListener {
                    listener?.onLongClick(msgViewHolder.itemView, position)
                    return@setOnLongClickListener true
                }
            }

            Constants.ChatViewType.TEXT_MESSAGE -> {
                val msgViewHolder = holder as TextMessageViewHolder
                msgViewHolder.binding.tvContent.text = chatModel.message_model!!.messageString
                msgViewHolder.binding.tvDateTime.text = chatModel.message_date
                msgViewHolder.binding.tvDateTime.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    override fun getItemId(position: Int): Long {
        return list[position].messageId.toLong()
    }


    inner class TextMessageViewHolder(val binding: FriendChatItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TextMessageSelfViewHolder(val binding: MyChatItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
