package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkAllReadModel (
    val statusCode: Long? = null,
    val data: Data? = null
):Parcelable{
    @Parcelize
    data class Data (
        val res: Res? = null
    ):Parcelable

    @Parcelize
    data class Res (
        val n: Long? = null,
        val nModified: Long? = null,
        val ok: Long? = null
    ):Parcelable

}

