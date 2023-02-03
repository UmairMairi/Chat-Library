package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageDeliveredModal(
    val deliveredAt: String,
    val messageId: String,
    val receiverId: String,
    val senderId: String,
    val status: Int
):Parcelable