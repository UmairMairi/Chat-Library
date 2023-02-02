package com.xint.chatlibrary.core

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

    fun initialization(socketUrl:String,baseUrl:String){
        this.socketUrl = socketUrl
        this.baseUrl = baseUrl
    }
}