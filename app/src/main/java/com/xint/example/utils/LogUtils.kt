package com.xint.example.utils

import android.util.Log
import com.xint.example.BuildConfig

object LogUtils {

    fun verbose(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && message != null) Log.v(tag, message)
    }

    fun verbose(message: String?) {
        if (BuildConfig.DEBUG && message != null) Log.v("TAG", message)
    }

    fun debug(tag: String?, message: String?) {
        if (BuildConfig.DEBUG && message != null) Log.d(tag, message)
    }

    fun error(message: String?) {
        if (BuildConfig.DEBUG && message != null) Log.e("TAG", message)
    }

    fun exception(e: Exception) {
        if (BuildConfig.DEBUG) e.printStackTrace()
    }

    fun exception(e: Exception?, request: String?, response: String?) {}

    fun exception(e: Exception?, request: String?) {}
}
