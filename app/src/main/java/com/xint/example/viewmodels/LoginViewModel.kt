package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.repository.OnBoardingRepository
import com.xint.example.extentions.getErrorMsg
import com.xint.example.utils.LogUtils
import org.json.JSONObject

class LoginViewModel:ViewModel() {
    var loading: MutableLiveData<Boolean>? = null
    var errorMsg: MutableLiveData<String>? = null
    var sendOTPResponse: MutableLiveData<JSONObject>? = null

    init {
        loading = MutableLiveData<Boolean>()
        loading?.value = false

        errorMsg = MutableLiveData<String>()
        errorMsg?.value = ""

        sendOTPResponse = MutableLiveData<JSONObject>()

    }

    fun sendOtp(mobileNo:String,reason:String){
        OnBoardingRepository.callSendOTP(
            mobileNo = mobileNo,
            reason = reason,
            listener = object:ResponseListener{
            override fun onLoading(isLoading: Boolean) {
                loading?.value = isLoading
            }

            override fun onSuccess(responseBody: JSONObject) {
                LogUtils.debug("Sent Otp Response-->","$responseBody")
                sendOTPResponse?.value = responseBody
            }

            override fun onErrorBody(responseBody: JSONObject) {
                errorMsg?.value = responseBody.getErrorMsg()
            }

            override fun onFailure(error: String) {
                errorMsg?.value = error
            }
        })
    }

}