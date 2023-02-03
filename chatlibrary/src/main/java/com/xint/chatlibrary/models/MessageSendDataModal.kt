package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageSendDataModal(
    val chatType: Int,
    val conversationId: String,
    val groupId: String,
    val messageContent: String,
    val messageType: Int,
    val mediaIdentifier: String,
    val receiverId: String,
    val metadata: Metadata?
) : Parcelable {
    @Parcelize
    data class Metadata(
        val mime: String? = null,
        val url: String? = null,
        val previewUrl: String? = null,
        val lat: String? = null,
        val long: String? = null,
        val address: String? = null,
        val addressTitle: String? = null,
        val amount: String? = null,
        val transactionId: String? = null,
        val status: String? = null
    ) : Parcelable
}