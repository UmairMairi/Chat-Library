package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUnreadCountModel (
    val statusCode: Long? = null,
    val data: Data? = null
):Parcelable{
    @Parcelize
    data class Data (
        val unreadCount: Long? = null
    ):Parcelable
}

