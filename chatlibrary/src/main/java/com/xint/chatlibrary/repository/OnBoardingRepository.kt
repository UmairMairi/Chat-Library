package com.xint.chatlibrary.repository

import com.google.gson.JsonObject
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.network.ApiClient
import com.xint.chatlibrary.network.ApiInterface
import okhttp3.RequestBody

object OnBoardingRepository {

    fun callSendOTP(mobileNo: String, reason: String?, listener: ResponseListener) {
        val data = JsonObject()
        data.addProperty("mobileNo", mobileNo)
        if (reason!=null) data.addProperty("reason", reason)
        val call = ApiClient.getRetrofit(requiredToken = false)!!.create(ApiInterface::class.java).sendOtp(data)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun callVerifyOTP(
        mobileNo: String,
        tId: String,
        otp: String,
        reason: String?,
        uId: String?,
        licExpiry: String?,
        listener: ResponseListener) {

        val data = JsonObject()
        data.addProperty("mobileNo", mobileNo)
        data.addProperty("otp", otp)
        data.addProperty("tId", tId)
        if (reason != null) data.addProperty("reason", reason)
        if (uId != null) data.addProperty("userId", uId)
        if (licExpiry != null) data.addProperty("licExpiry", licExpiry)
        val call = ApiClient.getRetrofit(requiredToken = false)!!.create(ApiInterface::class.java).verifyOtp(data)
        listener.onLoading(isLoading = true)
        call?.let { ApiClient.retrofitCall(call = it, listener = listener) }

    }

}