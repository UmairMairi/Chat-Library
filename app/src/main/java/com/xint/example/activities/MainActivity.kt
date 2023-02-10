package com.xint.example.activities

import android.os.Bundle
import com.xint.chatlibrary.core.ChatCore
import com.xint.example.LogUtils
import com.xint.example.adapters.ConversationAdapter
import com.xint.example.databinding.ActivityConversationBinding

class MainActivity : BaseActivity() {
    private lateinit var adapter: ConversationAdapter
    private lateinit var binding: ActivityConversationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ChatCore.instance?.initialization(
            context = this@MainActivity,
            socketUrl = "http://150.230.54.239:8100",
            baseUrl = "http://150.230.54.239:3000",
            sessionId =  "48762736523652396978436987468794365"
        )

        LogUtils.debug("BaseUrl", ChatCore.instance?.baseUrl)
        LogUtils.debug("socketUrl", ChatCore.instance?.socketUrl)

        ChatCore.instance?.subscribeUser(userId = "f67628a0-9022-4b58-85fb-0f1f8612429b")

        binding.rvChat.adapter = ConversationAdapter()
    }
}