package com.xint.example.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xint.chatlibrary.core.ChatCore
import com.xint.example.utils.Constants
import com.xint.example.viewmodels.ConversationsViewModel
import com.xint.example.utils.LogUtils
import com.xint.example.adapters.ConversationAdapter
import com.xint.example.databinding.ActivityConversationBinding
import com.xint.example.model.GetConversationsModel
import com.xint.example.utils.Singleton

class MainActivity : BaseActivity() {
    private lateinit var adapter: ConversationAdapter
    private lateinit var binding: ActivityConversationBinding
    private lateinit var viewModel: ConversationsViewModel
    private lateinit var list:  ArrayList<GetConversationsModel.Datum>
    private lateinit var filteredlist:  ArrayList<GetConversationsModel.Datum>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConversationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ConversationsViewModel::class.java]
        list = ArrayList()
        adapter = ConversationAdapter(context = this@MainActivity,list =  list)
        binding.rvChat.adapter =  adapter
        setContentView(binding.root)
        ChatCore.instance?.initialization(
            context = this@MainActivity,
            socketUrl = Constants.socketUrl,
            baseUrl = Constants.serverUrl,
            sessionId =  Constants.sessionId)

        Singleton.instance?.userId = Constants.userId

        ChatCore.instance?.subscribeUser(userId = Constants.userId)
        viewModel.let {vm->
            vm.getConversation()
            vm.loading?.observe(this){ loading->
                showDismissLoader(isLoading = loading)
            }
            vm.errorMsg?.observe(this){ msg->
                if(msg!=null && msg != ""){
                    LogUtils.error("ViewModel Error--->$msg")
                }
            }
            vm.conversationsModel?.observe(this){
                try{
                    val model = Gson().fromJson(it.toString(),GetConversationsModel::class.java)
                    model?.data?.let {list->
                        this.list.addAll(list)
                        binding.rvChat.adapter?.notifyDataSetChanged()
                    }
                }catch (e:Exception){
                    vm.errorMsg?.value = e.message
                }
            }
        }
    }
}