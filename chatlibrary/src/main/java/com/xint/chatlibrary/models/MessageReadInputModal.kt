package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageReadInputModal(
    val Data: MessageReadInputDataModal,
    val userID: String
) : Parcelable {


    @Parcelize
    data class MessageReadInputDataModal(
        val messageId: String,
        val receiverId: String,
        val senderId: String
    ) : Parcelable
}