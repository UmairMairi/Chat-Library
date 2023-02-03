package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploadMediaModel(
    val statusCode: Int,
    val data: Data
):Parcelable{
    @Parcelize
    data class Data(
        val mediaUrl: String,
    ) : Parcelable
}