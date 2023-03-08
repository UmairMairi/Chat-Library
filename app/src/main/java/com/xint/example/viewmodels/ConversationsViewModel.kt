package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.repository.ChatRepository
import com.xint.example.extentions.getErrorMsg
import com.xint.example.model.GetConversationsModel
import com.xint.example.utils.LogUtils
import org.json.JSONObject

class ConversationsViewModel:ViewModel() {
    var loading: MutableLiveData<Boolean>? = null
    var errorMsg: MutableLiveData<String>? = null
    var conversationsModel: MutableLiveData<JSONObject>? = null
    init {
        loading = MutableLiveData<Boolean>()
        loading?.value = false

        errorMsg = MutableLiveData<String>()
        errorMsg?.value = ""

        conversationsModel = MutableLiveData<JSONObject>()

    }

    fun getConversation(){
        ChatRepository.getChatConversations(listener = object:ResponseListener{
            override fun onLoading(isLoading: Boolean) {
                loading?.value = isLoading
            }

            override fun onSuccess(responseBody: JSONObject) {
                LogUtils.debug("Get Conversations-->","$responseBody")
                conversationsModel?.value = responseBody
            }

            override fun onErrorBody(responseBody: JSONObject) {
                errorMsg?.value = responseBody.getErrorMsg()
            }

            override fun onFailure(error: String) {
                errorMsg?.value = error
            }
        })
    }

//    fun getFilterList(compareWith:String,list:ArrayList<GetConversationsModel.Datum>):ArrayList<GetConversationsModel.Datum>{
//        val resultList = ArrayList<GetConversationsModel.Datum>()
//        for (item in list){
//        }
//
//        return resultList
//    }
}