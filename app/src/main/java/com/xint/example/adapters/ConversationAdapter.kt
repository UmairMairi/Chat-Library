package com.xint.example.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xint.example.R
import com.xint.example.databinding.ItemContactsBinding
import com.xint.example.model.GetConversationsModel
import com.xint.example.utils.DateTimeUtils

class ConversationAdapter(var list :List<GetConversationsModel.Datum>):RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemContactsBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvMessage.text = list[position].lastMessage?.messageContent ?: ""
        holder.binding.tvTime.text = DateTimeUtils.formatDateFromString(inputDate =  DateTimeUtils.getDateTimeFromMillis(milliSeconds = list[position].lastMessage?.timestamp ?: 0), outputFormat = DateTimeUtils.timeFormat)
        when (list[position].lastMessage?.status.toString()) {
            "0" -> {
                holder.binding.ivSend.visibility = View.VISIBLE
                holder.binding.ivSend.setImageResource(R.drawable.icon_clock)
            }
            "1" -> {
                holder.binding.ivSend.visibility = View.VISIBLE
                holder.binding.ivSend.setImageResource(R.drawable.icon_tick)
            }
            "2" -> {
                holder.binding.ivSend.visibility = View.VISIBLE
                holder.binding.ivSend.setImageResource(R.drawable.icon_tick)
            }
            "3" -> {
                holder.binding.ivSend.visibility = View.VISIBLE
                holder.binding.ivSend.setImageResource(R.drawable.icon_double_tick_grey)
            }
            "4" -> {
                holder.binding.ivSend.visibility = View.VISIBLE
                holder.binding.ivSend.setImageResource(R.drawable.icon_double_tick)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}