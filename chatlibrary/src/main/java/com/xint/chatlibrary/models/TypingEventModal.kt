package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TypingEventModal(
    val senderId: String,
    val receiverId: String,
    val conversationId: String,
    val action: String
):Parcelable