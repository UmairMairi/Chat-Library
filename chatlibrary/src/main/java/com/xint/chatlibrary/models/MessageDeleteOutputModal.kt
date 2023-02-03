package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageDeleteOutputModal(
    val _id: String,
    val deletedAt: String,
    val receiverId: String,
    val senderId: String
):Parcelable