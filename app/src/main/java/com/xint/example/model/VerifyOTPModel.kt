package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VerifyOTPModel(
    val statusCode: Long? = null,
    val data: Data? = null
) : Parcelable {
    @Parcelize
    data class Data(
        val token: String? = null,
        val details: UserDetailsModel.User? = null
    ) : Parcelable
}