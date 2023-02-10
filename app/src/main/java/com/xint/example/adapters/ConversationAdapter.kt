package com.xint.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xint.example.databinding.ItemContactsBinding

class ConversationAdapter:RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemContactsBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding
    }

    override fun getItemCount(): Int {
        return 10
    }
}