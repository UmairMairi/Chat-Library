package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnblockUserInputModal(
    val data: UnblockUserInputDataModal,
    val userID: String
) : Parcelable {
    @Parcelize
    data class UnblockUserInputDataModal(
        val receiverId: String,
        val senderId: String
    ) : Parcelable
}