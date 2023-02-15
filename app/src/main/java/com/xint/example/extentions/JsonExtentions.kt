package com.xint.example.extentions

import org.json.JSONArray
import org.json.JSONObject

fun JSONObject?.getErrorMsg(): String {
    if (this == null) return "Something went wrong"
    try {
        return if (this.has("message")) {
            when (this["message"]) {
                is JSONObject -> {
                    this.getString("message")
                }
                is JSONArray -> {
                    "${this.getJSONArray("message")[0]}"
                }
                else -> {
                    this.getString("message")
                }
            }
        }else{
            "Something went wrong"
        }
    } catch (e: Exception) {
        return "Something went wrong"
    }
}