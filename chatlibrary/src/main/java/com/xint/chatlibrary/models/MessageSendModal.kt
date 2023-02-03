package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageSendModal(
    val data : MessageSendDataModal,
    val userID: String
):Parcelable