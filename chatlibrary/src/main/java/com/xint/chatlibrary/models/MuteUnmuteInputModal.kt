package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MuteUnmuteInputModal(
    val data: MuteUnmuteInputDataModal,
    val userID: String
) : Parcelable {
    @Parcelize
    data class MuteUnmuteInputDataModal(
        val conversationId: String,
        val status: Boolean
    ) : Parcelable
}