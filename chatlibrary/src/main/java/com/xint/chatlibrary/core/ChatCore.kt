package com.xint.chatlibrary.core

import android.content.Context
import com.xint.chatlibrary.services.SocketTasks
import com.xint.chatlibrary.utils.ChatConstants
import org.json.JSONObject

class ChatCore {

    lateinit var socketUrl:String
    lateinit var baseUrl:String
    companion object {
        private var mInstance: ChatCore? = null

        @get:Synchronized
        val instance: ChatCore?
            get() {
                if (mInstance == null) {
                    mInstance = ChatCore()
                }
                return mInstance
            }
    }

    fun initialization(context: Context, socketUrl:String, baseUrl:String){
        this.socketUrl = socketUrl
        this.baseUrl = baseUrl
        SocketTasks.initializeSocket(context = context)
    }

    fun subscribeUser(userId:String){
        val jsonObject  = JSONObject()
        jsonObject.put(ChatConstants.SubscribeUser.userId,userId)
        SocketTasks.subscribeUser(request = jsonObject)
    }
}