package com.xint.chatlibrary.repository

import com.xint.chatlibrary.listeners.ResponseListener
import com.xint.chatlibrary.network.ApiClient
import com.xint.chatlibrary.network.ApiInterface
import okhttp3.RequestBody

object ChatRepository {
    fun getChatConversations(listener: ResponseListener, page: Int = 1, limit: Int = 1) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getConversation(page = page, limit = limit)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getArchivedChats(listener: ResponseListener, page: Int, limit: Int) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getArchivedConversations(page = page, limit = limit)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getBlockedChats(listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java).getBlockedUsers()
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getUserMessages(userId: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getUserMessages(userId = userId)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getUserDetails(receiverId: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getUserDetails(receiverId = receiverId)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getLastSeen(receiverId: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getLastSeen(receiverId = receiverId)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }


    fun updateLastSeen(receiverId: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .updateLastSeen(receiverId = receiverId)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }


    fun deleteAllMessages(
        conversationId: String,
        requestBody: RequestBody,
        listener: ResponseListener
    ) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .deleteAllMessage(conversationId = conversationId, requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun callArchiveChatApi(
        conversationId: String,
        requestBody: RequestBody,
        listener: ResponseListener
    ) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .archiveUnarchiveChat(conversationId = conversationId, requestBody = requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun markAllReadApi(requestBody: RequestBody, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .markAllRead(requestBody = requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getUnreadMessageCount(listener: ResponseListener) {
        val call =
            ApiClient.getRetrofit()!!.create(ApiInterface::class.java).getUnreadMessageCount()
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun getPreSignedUrl(requestBody: RequestBody, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .getPreSignedUrl(requestBody = requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun uploadPreSignedUrl(requestBody: RequestBody, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .uploadPreSignedFile(requestBody = requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun uploadBinaryMedia(requestBody: RequestBody, url: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java)
            .uploadBinaryMedia(url = url, requestBody = requestBody)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }

    fun dynamicUrl(url: String, listener: ResponseListener) {
        val call = ApiClient.getRetrofit()!!.create(ApiInterface::class.java).dynamicUrl(url = url)
        listener.onLoading(isLoading = true)
        call?.let {
            ApiClient.retrofitCall(call = it, listener = listener)
        }
    }
}