package com.xint.example

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import com.xint.example.utils.PrefManager


class Ride : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        PrefManager.init(this)
    }

}