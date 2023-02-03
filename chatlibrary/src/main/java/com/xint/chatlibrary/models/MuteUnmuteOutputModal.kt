package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MuteUnmuteOutputModal(
    val conversationId: String,
    val status: Boolean,
    val userId: String
):Parcelable