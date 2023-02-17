package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.repository.ChatRepository
import com.xint.example.extentions.getErrorMsg
import org.json.JSONObject

class ChatActivityViewModel: ViewModel() {
    var loading: MutableLiveData<Boolean>? = null
    var errorMsg: MutableLiveData<String>? = null
    var userMessages: MutableLiveData<JSONObject>? = null
    init {
        loading = MutableLiveData<Boolean>()
        loading?.value = false
        errorMsg = MutableLiveData<String>()
        errorMsg?.value = ""
        userMessages = MutableLiveData<JSONObject>()

    }

    fun getUserMessages(userId:String){
        ChatRepository.getUserMessages(
            userId = userId,
            listener = object: ResponseListener {
            override fun onLoading(isLoading: Boolean) {
                loading?.value = isLoading
            }

            override fun onSuccess(responseBody: JSONObject) {
                userMessages?.value = responseBody
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