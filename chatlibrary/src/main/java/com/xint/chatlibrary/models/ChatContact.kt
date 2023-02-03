package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open  class ChatContact(
    var _id: String = "",
    var _vs: Int = 0,
    var blocked: Boolean = false,
    var chatType: Int = 0,
    var createdAt: String = "",
    var initiator: Int = 0,
    var lastMessage: LastMessage? = null,
    var muted: Boolean = false,
    var receiver: Receiver? = null,
    //var group: List<ReceiverX>? = null,
    var unread: Int = 0,
    var updatedAt: String = "",
) : Parcelable{
    @Parcelize
    open class LastMessage(
        var createdAt: String? = "",
        var timestamp: Long? = 0L,
        var lastMessageBy: String? = "",
        var messageType: String? = "",
        var messageContent: String? = "",
    ) : Parcelable

    @Parcelize
    open class Receiver(
//    val fullName: String,
//    val fullNameArabic: String,
        var englishName: String = "",
        var arabicName: String ="",
        var profileImage: String ="",
        var userId: Int = 0
    ) : Parcelable
}