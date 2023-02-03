package com.xint.chatlibrary.listeners

import org.json.JSONObject

interface ResponseListener {
    fun onLoading(isLoading: Boolean)
    fun onSuccess(responseBody: JSONObject)
    fun onErrorBody(responseBody: JSONObject)
    fun onFailure(error: String)
}