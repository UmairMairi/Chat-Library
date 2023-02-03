package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TypingEventInputModal(
    val data: TypingEventInputDataModal,
    val userID: String
) : Parcelable {
    @Parcelize
    data class TypingEventInputDataModal(
        val conversationId: String,
        val receiverId: String,
        val action: String
    ) : Parcelable
}