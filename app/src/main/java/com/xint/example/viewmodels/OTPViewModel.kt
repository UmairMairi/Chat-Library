package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.repository.OnBoardingRepository
import com.xint.example.extentions.getErrorMsg
import com.xint.example.utils.LogUtils
import org.json.JSONObject

class OTPViewModel:ViewModel() {
    var loading: MutableLiveData<Boolean>? = null
    var errorMsg: MutableLiveData<String>? = null
    var verifyOTPResponse: MutableLiveData<JSONObject>? = null
    init {
        loading = MutableLiveData<Boolean>()
        loading?.value = false

        errorMsg = MutableLiveData<String>()
        errorMsg?.value = ""

        verifyOTPResponse = MutableLiveData<JSONObject>()

    }

    fun verifyOTP(mobileNo: String,otp: String, tId: String, reason: String? = null, uId: String? = null, licExpiry: String? = null) {
        OnBoardingRepository.callVerifyOTP(
            mobileNo = mobileNo,
            otp = otp,
            tId = tId,
            reason = reason,
            uId = uId,
            licExpiry = licExpiry,
            listener = object : ResponseListener {
                override fun onLoading(isLoading: Boolean) {
                    LogUtils.debug("IsLoading-->", isLoading.toString())
                    loading?.value = isLoading

                }

                override fun onSuccess(responseBody: JSONObject) {
                    LogUtils.debug("Verify OTP response-->", responseBody.toString())
                    verifyOTPResponse?.value = responseBody
                }

                override fun onErrorBody(responseBody: JSONObject) {
                    errorMsg?.value = responseBody.getErrorMsg()
                }

                override fun onFailure(error: String) {
                    errorMsg?.value
                }
            }
        )
    }


}