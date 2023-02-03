package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class
BlockUserInputModal(
    val data: BlockUserInputDataModal,
    val userID: String
) : Parcelable {
    @Parcelize
    data class BlockUserInputDataModal(
        val receiverId: String,
        val senderId: String
    ) : Parcelable
}