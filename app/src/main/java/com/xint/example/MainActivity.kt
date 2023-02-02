package com.xint.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xint.chatlibrary.core.ChatCore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ChatCore.instance?.initialization("text","test2")

        LogUtils.debug("BaseUrl",ChatCore.instance?.baseUrl)
        LogUtils.debug("socketUrl",ChatCore.instance?.socketUrl)
    }
}