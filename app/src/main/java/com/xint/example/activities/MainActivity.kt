package com.xint.example.activities

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

class MainActivity : BaseActivity() {
    private lateinit var adapter: ConversationAdapter
    private lateinit var binding: ActivityConversationBinding
    private lateinit var viewModel: ConversationsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConversationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ConversationsViewModel::class.java]

        setContentView(binding.root)
        ChatCore.instance?.initialization(
            context = this@MainActivity,
            socketUrl = Constants.socketUrl,
            baseUrl = Constants.serverUrl,
            sessionId =  Constants.sessionId)

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
                        binding.rvChat.adapter = ConversationAdapter(context = this@MainActivity,list =  list)
                    }
                }catch (e:Exception){
                    vm.errorMsg?.value = e.message
                }
            }
        }
    }
}