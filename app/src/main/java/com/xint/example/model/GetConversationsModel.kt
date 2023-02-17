package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetConversationsModel(
    val statusCode: Long? = null,
    val data: List<Datum>? = null
):Parcelable{
    @Parcelize
    data class Datum (
        val _id: String? = null,

        val archivedFor:List<ArchivedFor>? = null,
        val initiator: Long? = null,
        val chatType: Long? = null,
        val createdAt: Long? = null,
        val updatedAt: Long? = null,
        val _vs: Long? = null,
        val lastMessage: LastMessage? = null,
        val muted: Boolean? = null,
        val unread: Long? = null,
        val receiver: Receiver? = null,
        val blocked: Boolean? = null
    ):Parcelable{

        @Parcelize
        data class ArchivedFor (
            val userId: Long? = null
        ):Parcelable

        @Parcelize
        data class LastMessage (
            val _id: String? = null,
            val messageId: String? = null,
            val timestamp: Long? = null,
            val messageType: Long? = null,
            val messageContent: String? = null,
            val status: Long? = null,
            val createdAt: String? = null
        ):Parcelable

        @Parcelize
        data class Receiver (
            val userId: Long? = null,
            val englishName: String? = null,
            val mobileNo: String? = null
        ):Parcelable
    }
}