package com.rider.ride.network.models

import android.os.Parcelable
import com.xint.chatlibrary.models.MessageSendDataModal
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessageApiModal(
    val statusCode: Int,
    val data: List<Messages>
) : Parcelable {
    @Parcelize
    data class Messages(
        val _id: String,
        val conversationId: String,
        val chatType: Int,
        val createdAt: String,
        val messageContent: String,
        val messageId: String,
        val messageType: Int,
        val metadata: MessageSendDataModal.Metadata?,
        val senderId: Long,
        val receiverId: Long,
        var receiver: ReceiverStatus? = null,
        val status: Int,
        val timestamp: Long,
        val updatedAt: String
    ) : Parcelable {
        @Parcelize
        data class ReceiverStatus(val status: Int) : Parcelable
    }
}
