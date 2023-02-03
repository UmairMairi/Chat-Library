package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnblockUserOutputModal(
    val doneAt: String,
    val receiverId: String,
    val senderId: String
):Parcelable