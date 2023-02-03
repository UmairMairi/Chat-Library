package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageDeleteInputModal(
    val data: MessageDeleteInputDataModal,
    val userID: String
) : Parcelable {

    @Parcelize
    data class MessageDeleteInputDataModal(
        val _id: String,
        val receiverId: String,
        val senderId: String
    ) : Parcelable

}