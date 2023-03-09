package com.xint.example.utils

import android.app.Application

class Singleton : Application() {
    var userId: String? = null
    var mobileNo: String? = null
    var tId: String? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    fun clearData() {
        mInstance = Singleton()
    }

    companion object {
        private var mInstance: Singleton? = null

        @get:Synchronized
        val instance: Singleton?
            get() {
                if (mInstance == null) {
                    mInstance = Singleton()
                }
                return mInstance
            }
    }
}