package com.xint.chatlibrary.services

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.xint.chatlibrary.core.ChatCore
import com.xint.chatlibrary.listeners.ChatListener
import com.xint.chatlibrary.models.*
import com.xint.chatlibrary.utils.LibraryLogs
import com.xint.chatlibrary.utils.Utils
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SocketTasks {
    private const val TAG = "SocketTask-->"
    private const val RequestTAG = "SocketTask Request-->"
    private var retry = true
    private lateinit var mSocket: Socket
    private var options = IO.Options()

//    Socket Events

    private const val EVENT_SUBSCRIBE = "subscribe-user"
    private const val USER_GENERAL = "user-general"
    private const val UNSUBSCRIBE_USER = "unsubscribe-user" //subscribe


    //CHAT
    private const val SEND_MESSAGE = "send-message" //subscribe/publish
    private const val RECEIVE_MESSAGE = "receive-message" //subscribe
    private const val MESSAGE_DELIVERED = "message-delivered" //subscribe
    private const val MESSAGE_READ = "message-read" //subscribe/publish
    private const val DELETE_MESSAGE = "delete-message" //subscribe/publish
    private const val BLOCK_USER = "block-user" //subscribe/publish
    private const val UNBLOCK_USER = "unblock-user" //subscribe/publish
    private const val TYPING_EVENT = "typing-event" //subscribe/publish
    private const val SEND_MESSAGE_ACK = "send-message-ack" //subscribe
    private const val CONVERSATION_GET = "conversation-get" //subscribe/publish
    private const val UPDATE_CHAT_USER_LAST_SEEN = "update-chat-user-last-seen" //publish
    private const val GET_CHAT_USER_LAST_SEEN = "get-chat-user-last-seen" //subscribe
    private const val MUTE_UNMUTE_CONVERSATION = "mute-unmute-conversation" //publish/subscribe



    init {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
        options.callFactory = clientBuilder.build()
        mSocket = IO.socket(ChatCore.instance?.socketUrl, options)
        mSocket.io().timeout(-1)
    }

    fun initializeSocket(context: Context) {

        mSocket.connect()

        mSocket.on(Socket.EVENT_CONNECT) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_CONNECT}", it.toString())
            }
            LibraryLogs.debug(TAG, Socket.EVENT_CONNECT)
//            val bundle = Bundle()
//            val intent = Intent(Constants.SOCKET_TASK)
//            intent.putExtras(bundle)
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        mSocket.on(Socket.EVENT_DISCONNECT) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_DISCONNECT}", it.toString())
            }
            if (retry){
                mSocket.connect()
            }
        }

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG ${Socket.EVENT_CONNECT_ERROR}", it.toString())
            }

            if (retry){
                mSocket.connect()
            }

        }

        mSocket.on(USER_GENERAL) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $EVENT_SUBSCRIBE", it.toString())
            }
        }

    }

    fun disconnect() {
        retry = false
        mSocket.disconnect()
    }

    fun isSocketConnected(): Boolean {
        return mSocket.connected()
    }

    fun subscribeUser(request:JSONObject) {
        LibraryLogs.debug(RequestTAG, "$request")
        mSocket.emit(EVENT_SUBSCRIBE, request)
    }


    /**Chat Listeners**/

    //Chat sockets
    fun publishSendMessage(data: MessageSendModal) {

        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        val jsonObjectMetaData = JSONObject()

        jsonObject.put("userID", data.userID)

        jsonObjectData.put("receiverId", data.data.receiverId)
        jsonObjectData.put("groupId", data.data.groupId)
        jsonObjectData.put("conversationId", data.data.conversationId)
        jsonObjectData.put("chatType", data.data.chatType)
        jsonObjectData.put("messageType", data.data.messageType)
        jsonObjectData.put("messageContent", data.data.messageContent)
        jsonObjectData.put("mediaIdentifier", data.data.mediaIdentifier)

        if (!data.data.metadata?.url.isNullOrEmpty())
            jsonObjectMetaData.put("url", data.data.metadata?.url)
        if (!data.data.metadata?.mime.isNullOrEmpty())
            jsonObjectMetaData.put("mime", data.data.metadata?.mime)
        if (!data.data.metadata?.previewUrl.isNullOrEmpty())
            jsonObjectMetaData.put("previewUrl", data.data.metadata?.previewUrl)
        if (!data.data.metadata?.lat.isNullOrEmpty())
            jsonObjectMetaData.put("lat", data.data.metadata?.lat?.toDouble())
        if (!data.data.metadata?.long.isNullOrEmpty())
            jsonObjectMetaData.put("long", data.data.metadata?.long?.toDouble())
        if (!data.data.metadata?.address.isNullOrEmpty())
            jsonObjectMetaData.put("address", data.data.metadata?.address)
        if (!data.data.metadata?.addressTitle.isNullOrEmpty())
            jsonObjectMetaData.put("addressTitle", data.data.metadata?.addressTitle)
        if (!data.data.metadata?.amount.isNullOrEmpty())
            jsonObjectMetaData.put("amount", data.data.metadata?.amount?.toDouble())
        if (!data.data.metadata?.transactionId.isNullOrEmpty())
            jsonObjectMetaData.put("transactionId", data.data.metadata?.transactionId)
        if (!data.data.metadata?.status.isNullOrEmpty())
            jsonObjectMetaData.put("status", data.data.metadata?.status)

        jsonObjectData.put("metadata", jsonObjectMetaData)

        jsonObject.put("data", jsonObjectData)
        if (data.data.conversationId.length == 24 || data.data.conversationId.isEmpty()) {
            mSocket.emit(SEND_MESSAGE, jsonObject)
        } else {
            val length = data.data.conversationId.length
            LibraryLogs.error("Length error on conversation id $length")
        }

        LibraryLogs.debug("$TAG $SEND_MESSAGE", jsonObject.toString())
    }

    fun subscribeSendMessage(listener: ChatListener) {
        if (mSocket.hasListeners(SEND_MESSAGE)) {
            mSocket.off(SEND_MESSAGE)
        }
        mSocket.on(SEND_MESSAGE) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $SEND_MESSAGE", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageReceiveModal::class.java
                )
                listener.onReceiveMessage(modal)
            }
        }
    }

    fun subscribeReceiveMessage(listener: ChatListener) {
        if (mSocket.hasListeners(RECEIVE_MESSAGE)) {
            mSocket.off(RECEIVE_MESSAGE)
        }
        mSocket.on(RECEIVE_MESSAGE) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $RECEIVE_MESSAGE", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageReceiveModal::class.java
                )
                listener.onReceiveMessage(modal)
            }
        }
    }

    fun subscribeMessageDelivered(listener: ChatListener) {
        if (mSocket.hasListeners(MESSAGE_DELIVERED)) {
            mSocket.off(MESSAGE_DELIVERED)
        }
        mSocket.on(MESSAGE_DELIVERED) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $MESSAGE_DELIVERED", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageDeliveredModal::class.java
                )
                listener.onMessageDelivered(modal)
            }
        }
    }

    fun subscribeMessageRead(listener: ChatListener) {
        if (mSocket.hasListeners(MESSAGE_READ)) {
            mSocket.off(MESSAGE_READ)
        }
        mSocket.on(MESSAGE_READ) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $MESSAGE_READ", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageReadOutputModal::class.java
                )
                listener.onMessageRead(modal)
            }
        }
    }

    fun publishMessageRead(data: MessageReadInputModal) {
        val jsonObject = JSONObject()
        val jsonObject2 = JSONObject()
        jsonObject.put("messageId", data.Data.messageId)
        jsonObject.put("senderId", data.Data.senderId)
        jsonObject.put("receiverId", data.Data.receiverId)
        jsonObject2.put("userID", data.userID)
        jsonObject2.put("data", jsonObject)

        mSocket.emit(MESSAGE_READ, jsonObject2)
        LibraryLogs.debug("$TAG $MESSAGE_READ", jsonObject2.toString())
    }

    fun subscribeMessageDelete(listener: ChatListener) {
        if (mSocket.hasListeners(DELETE_MESSAGE)) {
            mSocket.off(DELETE_MESSAGE)
        }
        mSocket.on(DELETE_MESSAGE) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $DELETE_MESSAGE", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageDeleteOutputModal::class.java
                )
                listener.onMessageDelete(modal)
            }
        }
    }

    fun publishMessageDelete(data: MessageDeleteInputModal) {
        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        jsonObjectData.put("_id", data.data._id)
        jsonObjectData.put("senderId", data.data.senderId)
        jsonObjectData.put("receiverId", data.data.receiverId)
        jsonObject.put("userID", data.userID)
        jsonObject.put("data", jsonObjectData)
        mSocket.emit(DELETE_MESSAGE, jsonObject)
        LibraryLogs.debug("$TAG $DELETE_MESSAGE", jsonObject.toString())
    }

    fun subscribeBlockUser(listener: ChatListener) {
        if (mSocket.hasListeners(BLOCK_USER)) {
            mSocket.off(BLOCK_USER)
        }
        mSocket.on(BLOCK_USER) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $BLOCK_USER", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    BlockUserOutputModal::class.java
                )
                listener.onBlockUser(modal)
            }
        }
    }

    fun publishBlockUser(data: BlockUserInputModal) {
        val jsonObject = JSONObject()
        val jsonObject2 = JSONObject()
        jsonObject.put("senderId", data.data.senderId)
        jsonObject.put("receiverId", data.data.receiverId)
        jsonObject2.put("userID", data.userID)
        jsonObject2.put("data", jsonObject)
        mSocket.emit(BLOCK_USER, jsonObject2)
        LibraryLogs.debug("$TAG $BLOCK_USER", jsonObject2.toString())
    }

    fun subscribeUnblockUser(listener: ChatListener) {
        if (mSocket.hasListeners(UNBLOCK_USER)) {
            mSocket.off(UNBLOCK_USER)
        }
        mSocket.on(UNBLOCK_USER) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $UNBLOCK_USER", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    UnblockUserOutputModal::class.java
                )
                listener.onUnBlockUser(modal)
            }
        }
    }

    fun publishUnblockUser(data: UnblockUserInputModal) {
        val jsonObject = JSONObject()
        val jsonObject2 = JSONObject()
        jsonObject.put("senderId", data.data.senderId)
        jsonObject.put("receiverId", data.data.receiverId)
        jsonObject2.put("userID", data.userID)
        jsonObject2.put("data", jsonObject)
        mSocket.emit(UNBLOCK_USER, jsonObject2)
        LibraryLogs.debug("$TAG $UNBLOCK_USER", jsonObject2.toString())
    }

    fun subscribeMuteUnMute(listener: ChatListener) {
        if (mSocket.hasListeners(MUTE_UNMUTE_CONVERSATION)) {
            mSocket.off(MUTE_UNMUTE_CONVERSATION)
        }
        mSocket.on(MUTE_UNMUTE_CONVERSATION) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $MUTE_UNMUTE_CONVERSATION", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MuteUnmuteOutputModal::class.java
                )
                listener.onMuteUnMute(modal)
            }
        }
    }

    fun publishMuteUnMute(data: MuteUnmuteInputModal) {
        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        jsonObject.put("userID", data.userID)
        jsonObjectData.put("status", data.data.status)
        jsonObjectData.put("conversationId", data.data.conversationId)
        jsonObject.put("data", jsonObjectData)
        mSocket.emit(MUTE_UNMUTE_CONVERSATION, jsonObject)
        LibraryLogs.debug("$TAG $MUTE_UNMUTE_CONVERSATION", jsonObject.toString())
    }

    fun subscribeTypingEvent(listener: ChatListener) {
        if (mSocket.hasListeners(TYPING_EVENT)) {
            mSocket.off(TYPING_EVENT)
        }
        mSocket.on(TYPING_EVENT) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $TYPING_EVENT", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    TypingEventModal::class.java
                )
                listener.onTypingEvent(modal)
            }
        }
    }

    fun publishTypingEvent(data: TypingEventInputModal) {
        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        jsonObjectData.put("receiverId", data.data.receiverId)
        jsonObjectData.put("conversationId", data.data.conversationId)
        jsonObjectData.put("action", data.data.action)
        jsonObject.put("userID",data.userID)
        jsonObject.put("data", jsonObjectData)
        mSocket.emit(TYPING_EVENT, jsonObject)
        LibraryLogs.debug("$TAG $TYPING_EVENT", jsonObject.toString())
    }

    fun subscribeSendMessageAck(listener: ChatListener) {
        if (mSocket.hasListeners(SEND_MESSAGE_ACK)) {
            mSocket.off(SEND_MESSAGE_ACK)
        }
        mSocket.on(SEND_MESSAGE_ACK) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $SEND_MESSAGE_ACK", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    MessageReceiveModal::class.java
                )
                listener.onSendMessageAck(modal)
            }
        }
    }

    fun subscribeConversationGet(listener: ChatListener) {
        if (mSocket.hasListeners(CONVERSATION_GET)) {
            mSocket.off(CONVERSATION_GET)
        }
        mSocket.on(CONVERSATION_GET) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $CONVERSATION_GET", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    ChatContact::class.java
                )
                listener.onConversationGet(modal)
            }
        }
    }

    fun publishConversationGet(userId:String,conversationId: String) {
        val jsonObjectData = JSONObject()
        val jsonObject = JSONObject()
        jsonObjectData.put("conversationId", conversationId)
        jsonObject.put("userID", userId)
        jsonObject.put("data", jsonObjectData)
        mSocket.emit(CONVERSATION_GET, jsonObject)
        LibraryLogs.debug("$TAG $CONVERSATION_GET", jsonObject.toString())
    }

    fun subscribeGetChatUserLastSeen(listener: ChatListener) {
        if (mSocket.hasListeners(GET_CHAT_USER_LAST_SEEN)) {
            mSocket.off(GET_CHAT_USER_LAST_SEEN)
        }
        mSocket.on(GET_CHAT_USER_LAST_SEEN) { args ->
            args.forEach {
                LibraryLogs.debug("$TAG $GET_CHAT_USER_LAST_SEEN", it.toString())
                val modal = Utils.getObjectFromResponse(
                    it.toString(),
                    LastSeenModal::class.java
                )
                listener.onGetLastSeen(modal)
            }
        }
    }

    fun publishUpdateChatUserLastSeen(userId:String,data: Boolean) {
        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        jsonObjectData.put("onlineStatus", data)
        jsonObject.put("userID",userId)
        jsonObject.put("data", jsonObjectData)
        mSocket.emit(UPDATE_CHAT_USER_LAST_SEEN, jsonObject)
        LibraryLogs.debug("$TAG $UPDATE_CHAT_USER_LAST_SEEN", jsonObject.toString())
    }
}