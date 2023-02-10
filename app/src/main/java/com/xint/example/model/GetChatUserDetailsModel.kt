package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class GetChatUserDetailsModel (
    val statusCode: Long? = null,
    val data: Data? = null
):Parcelable{
    @Parcelize
    data class Data (
        val status: Long? = null,
        val mutedChat: @RawValue Any? = null,
        val _id: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val emailId: String? = null,
        val mobileNo: String? = null,
        val dateOfBirth: String? = null,
        val gender: String? = null,
        val deviceToken: String? = null,
        val clientOs: String? = null,
        val prefferedLanguage: String? = null,
        val userId: Long? = null,
        val englishName: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val _vs: Long? = null,
        val lastSeenAt: String? = null,
        val blocked: Boolean? = null
    ):Parcelable
}