package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreSignedDataModal(
    val statusCode: Int,
    val data: Data
):Parcelable{
    @Parcelize
    data class Data(
        val presignedUrl: String,
        val accessUrl: String,
        val params: Params,
        val thumbPresignedUrl: String,
        val thumbAccessUrl: String,
    ) : Parcelable {
        @Parcelize
        data class Params(
            val filename: String,
            val thumbnail: String,
            val mediaIdentifier: String,
        ) : Parcelable
    }
}