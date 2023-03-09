package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginModel(
    val statusCode: Long? = null,
    val data: Data? = null
) : Parcelable {
    @Parcelize
    data class Data(
        val tId: String? = null,
        val SmsApiResponse: SMSAPIResponse? = null
    ) : Parcelable {
        @Parcelize
        data class SMSAPIResponse(
            val message: String? = null
        ):Parcelable
    }
}


