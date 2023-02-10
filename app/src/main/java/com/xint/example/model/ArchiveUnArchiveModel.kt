package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArchiveUnArchiveModel (
    val statusCode: Long? = null,
    val data: Data? = null
):Parcelable{
    @Parcelize
    data class Data (
        val archivedFor: List<ArchivedFor>? = null,
        val deletedFor: List<ArchivedFor>? = null,
        val receivers: List<ArchivedFor>? = null,
        val _id: String? = null,
        val initiator: Long? = null,
        val chatType: Long? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val _vs: Long? = null
    ):Parcelable

    @Parcelize
    data class ArchivedFor (
        val userId: Long? = null
    ):Parcelable

}

