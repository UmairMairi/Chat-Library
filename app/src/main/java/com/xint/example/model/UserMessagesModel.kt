package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserMessagesModel(
    val statusCode: Long? = null,
    val data: List<Datum>? = null
) : Parcelable {
    @Parcelize
    data class Datum(
        val _id: String? = null,
        val conversationId: String? = null,
        val messageId: String? = null,
        val timestamp: Long? = null,
        val senderId: Long? = null,
        val receiverId: Long? = null,
        val chatType: Long? = null,
        val messageType: Long? = null,
        val messageContent: String? = null,
        val status: Long? = null,
        val metadata: Metadata? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val receiver: Receiver? = null
    ) : Parcelable {
        @Parcelize
        data class Metadata(
            val url: String? = null,
            val mime: String? = null
        ) : Parcelable

        @Parcelize
        data class Receiver(
            val status: Long? = null
        ) : Parcelable

    }
}
