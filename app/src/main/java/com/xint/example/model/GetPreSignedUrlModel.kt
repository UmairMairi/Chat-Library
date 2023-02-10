package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetPreSignedUrlModel(
    val statusCode: Long? = null,
    val data: Data? = null
) : Parcelable {
    @Parcelize
    data class Data(
        val presignedUrl: String? = null,
        val accessUrl: String? = null,
        val params: Params? = null
    ) : Parcelable {
        @Parcelize
        data class Params(
            val filename: String? = null
        ) : Parcelable
    }
}


