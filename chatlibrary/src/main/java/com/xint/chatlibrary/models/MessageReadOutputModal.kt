package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageReadOutputModal(
    val messageId: String,
    val readAt: String,
    val receiverId: String,
    val senderId: String,
    val status: Int
):Parcelable