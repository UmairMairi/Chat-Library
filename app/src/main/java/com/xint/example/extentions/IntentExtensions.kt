package com.xint.example.extentions

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

fun <T : Serializable?> Intent.getSerializable(key: String, myClass: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getSerializableExtra(key, myClass)!!
    else
        this.getSerializableExtra(key) as T
}

fun <T : Parcelable?> Intent.getParcelable(key: String, myClass: Class<T>): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        this.getParcelableExtra(key, myClass)!!
    else
        this.getSerializableExtra(key) as T
}