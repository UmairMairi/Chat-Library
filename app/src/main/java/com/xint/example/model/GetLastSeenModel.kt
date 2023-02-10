package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetLastSeenModel (
    val statusCode: Long? = null,
    val data: Data? = null
):Parcelable{
    @Parcelize
    data class Data (
        val onlineStatus: Boolean? = null
    ):Parcelable
}

