package com.xint.example.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.models.ChatMessageModel
import com.xint.chatlibrary.models.MessageModel
import com.xint.chatlibrary.models.MessageReceiveModal
import com.xint.chatlibrary.repository.ChatRepository
import com.xint.chatlibrary.utils.ChatConstants
import com.xint.example.extentions.getErrorMsg
import com.xint.example.utils.DateTimeUtils
import com.xint.example.utils.LogUtils
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

    fun prepareMessageForList(modal: MessageReceiveModal, self: Boolean):ChatMessageModel{
        when (modal.messageType) {
            1 -> {
                val data = Gson().toJson(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromMillis(inputFormat = DateTimeUtils.timeFormat,milliSeconds = modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = "",
                        viewType = if (self)
                            ChatConstants.Chat.ChatViewType.TEXT_MESSAGE_SELF
                        else
                            ChatConstants.Chat.ChatViewType.TEXT_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
                LogUtils.debug("Send Message-->", data)
                    return ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromMillis(inputFormat = DateTimeUtils.timeFormat,milliSeconds = modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = "",
                        viewType = if (self)
                            ChatConstants.Chat.ChatViewType.TEXT_MESSAGE_SELF
                        else
                            ChatConstants.Chat.ChatViewType.TEXT_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
            }
            2 -> {
                return ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromMillis(inputFormat = DateTimeUtils.timeFormat,milliSeconds =modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = "",
                        viewType = if (self) ChatConstants.Chat.ChatViewType.PICTURE_MESSAGE_SELF else ChatConstants.Chat.ChatViewType.PICTURE_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            imageUri = modal.metadata?.url,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
            }
            else->{return ChatMessageModel()}
        }
    }

}