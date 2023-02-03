package com.xint.chatlibrary.utils

import com.google.gson.Gson

object Utils {
    fun <T> getObjectFromResponse(response: String, convertToClass: Class<T>): T {
        val gson = Gson()
        return getObjectFromResponse(gson, response, convertToClass)
    }

    private fun <T> getObjectFromResponse(gson: Gson, response: String, convertToClass: Class<T>): T {
        return gson.fromJson(response, convertToClass)
    }

}