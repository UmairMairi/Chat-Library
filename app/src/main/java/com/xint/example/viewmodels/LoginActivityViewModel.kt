package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.repository.ChatRepository
import com.xint.example.extentions.getErrorMsg
import com.xint.example.model.GetConversationsModel
import com.xint.example.utils.LogUtils
import org.json.JSONObject

class LoginActivityViewModel:ViewModel() {
    var loading: MutableLiveData<Boolean>? = null
    var errorMsg: MutableLiveData<String>? = null
    init {
        loading = MutableLiveData<Boolean>()
        loading?.value = false

        errorMsg = MutableLiveData<String>()
        errorMsg?.value = ""


    }

}