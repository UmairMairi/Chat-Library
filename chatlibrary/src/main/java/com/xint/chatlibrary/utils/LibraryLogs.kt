package com.xint.chatlibrary.utils

import android.util.Log

object LibraryLogs {
    fun verbose(tag: String?, message: String?) {
        Log.v(tag, message ?: "Error Massage Not Found")
    }

    fun verbose(message: String?) {
        Log.v("TAG", message ?: "Error Massage Not Found")
    }

    fun debug(tag: String?, message: String?) {
        Log.d(tag, message ?: "Error Massage Not Found")
    }

    fun error(message: String?) {
        Log.e("TAG", message ?: "Error Massage Not Found")
    }

    fun exception(e: Exception) {
        e.printStackTrace()
    }

    fun exception(e: Exception?, request: String?, response: String?) {}

    fun exception(e: Exception?, request: String?) {}
}