package com.xint.example.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xint.chatlibrary.core.ChatCore
import com.xint.chatlibrary.services.SocketTasks
import com.xint.example.utils.Constants
import com.xint.example.viewmodels.ConversationsViewModel
import com.xint.example.utils.LogUtils
import com.xint.example.adapters.ConversationAdapter
import com.xint.example.databinding.ActivityConversationBinding
import com.xint.example.extentions.getParcelable
import com.xint.example.model.GetConversationsModel
import com.xint.example.model.VerifyOTPModel
import com.xint.example.utils.PrefManager
import com.xint.example.utils.Singleton

class MainActivity : BaseActivity() {
    private lateinit var adapter: ConversationAdapter
    private lateinit var binding: ActivityConversationBinding
    private lateinit var viewModel: ConversationsViewModel
    private lateinit var list: ArrayList<GetConversationsModel.Datum>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConversationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ConversationsViewModel::class.java]
        list = ArrayList()
        adapter = ConversationAdapter(context = this@MainActivity, list = list)
        binding.rvChat.adapter = adapter
        setContentView(binding.root)

        val loginModel = intent.getParcelable(Constants.OnBoarding.loginModel, VerifyOTPModel::class.java)

        loginModel.let {
            Singleton.instance?.userId = it.data?.details?.userId
            ChatCore.instance?.initialization(
                context = this@MainActivity,
                socketUrl = Constants.socketUrl,
                baseUrl = Constants.serverUrl,
                sessionId = it.data?.token?:"",
                userId = it.data?.details?.userId?:"",
            )
            ChatCore.instance?.subscribeUser("${Singleton.instance?.userId}")

        }

        viewModel.let { vm ->
            vm.getConversation()
            vm.loading?.observe(this) { loading ->
                showDismissLoader(isLoading = loading)
            }
            vm.errorMsg?.observe(this) { msg ->
                if (msg != null && msg != "") {
                    LogUtils.error("ViewModel Error--->$msg")
                    if (msg == "Token expired!") {
                        PrefManager.clear()
                        Singleton.instance?.clearData()
                        startActivity(
                            Intent(this@MainActivity, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                }
            }
            vm.conversationsModel?.observe(this) {
                try {
                    val model = Gson().fromJson(it.toString(), GetConversationsModel::class.java)
                    model?.data?.let { list ->
                        this.list.addAll(list)
                        binding.rvChat.adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    vm.errorMsg?.value = e.message
                }
            }
        }


    }

}