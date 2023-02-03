package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class LastMessage(
    var createdAt: String? = "",
    var timestamp: Long? = 0L,
    var lastMessageBy: String? = "",
    var messageType: String? = "",
    var messageContent: String? = "",
) : Parcelable