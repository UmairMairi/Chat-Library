package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageReceiveModal(
    val chatType: Int,
    val messageContent: String,
    val messageId: String,
    val messageType: Int,
    val receiverId: String,
    val senderId: String,
    val conversationId: String,
    val timestamp: String,
    val mediaIdentifier: String,
    val receiver: ReceiverStatus,
    val status: Int,
    val metadata : MessageSendDataModal.Metadata?
) : Parcelable{
    @Parcelize
    data class ReceiverStatus(val status: Int) : Parcelable
}