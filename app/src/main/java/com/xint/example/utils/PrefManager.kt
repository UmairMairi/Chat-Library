package com.xint.example.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created and coded by Shubham Mathur (OwnageByte) 10/3/21 2:22 PM
 **/
object PrefManager {
    private lateinit var sharedPreferences: SharedPreferences

    private const val PREF_NAME = "app_preferences"
    const val loginModel = "login-model"




    fun getPrefInstance():SharedPreferences? {
        return sharedPreferences ?: null
    }

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}