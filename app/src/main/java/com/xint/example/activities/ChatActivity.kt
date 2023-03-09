package com.xint.example.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.xint.chatlibrary.listeners.ChatListener
import com.xint.chatlibrary.models.*
import com.xint.chatlibrary.services.SocketTasks
import com.xint.chatlibrary.utils.ChatConstants
import com.xint.example.R
import com.xint.example.adapters.ChatAdapter
import com.xint.example.databinding.ActivityChatBinding
import com.xint.example.extentions.getParcelable
import com.xint.example.extentions.getSerializable
import com.xint.example.extentions.pushFragment
import com.xint.example.fragments.ChatControllerFragment
import com.xint.example.listeners.ChatControllerListener
import com.xint.example.model.GetConversationsModel
import com.xint.example.model.UserMessagesModel
import com.xint.example.utils.*
import com.xint.example.viewmodels.ChatActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ChatActivity : BaseActivity(), View.OnClickListener, ChatListener {

    private var binding: ActivityChatBinding? = null
    private var viewModel: ChatActivityViewModel? = null
    private var chatAdapter: ChatAdapter? = null
    private var list = ArrayList<ChatMessageModel>()


    private var senderProfileImage = ""
    private var onlineStatus = false

    private var mimeType = ""
    private var fileTextData = ""
    private lateinit var fileStore: File


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    it.data?.let { uri ->
                        LogUtils.debug("Picked File Path -->", uri.path)
                        val file = AppUtils.getFile(context = this@ChatActivity, uri = uri)
                        file.let { doc ->
                            filePreviewLauncher.launch(
                                Intent(
                                    this@ChatActivity,
                                    ImageVideoPreviewActivity::class.java
                                ).putExtras(
                                    bundleOf(
                                        Pair(Constants.ChatViewType.IMAGE_PREVIEW_PATH, doc.path),
                                        Pair(Constants.ChatViewType.FILE_TYPE, ChatConstants.Chat.ChatFileType.IMAGE)
                                    )
                                )
                            )
                        }

                    }
                }
            }
        }
    private var filePreviewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val filePath = data?.getStringExtra(Constants.ChatViewType.IMAGE_PREVIEW_PATH)
                fileTextData = data?.getStringExtra(Constants.ChatViewType.BODY).toString()
                LogUtils.debug("File_path", filePath)
                fileStore = filePath?.let { File(it) }!!
                mimeType = getMimeType(fileStore)
                Log.e("mimeType", mimeType)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this@ChatActivity)[ChatActivityViewModel::class.java]
        chatAdapter = ChatAdapter(list = list)
        binding!!.recyclerChat.adapter = chatAdapter

        val conversation = intent.getParcelable(
            Constants.KeysExtras.conversation,
            GetConversationsModel.Datum::class.java
        )
        print(conversation)
        viewModel?.let { vm ->
            vm.getUserMessages(userId = "${conversation.receiver?.userId ?: 0}")
            vm.loading?.observe(this) { loading ->
                showDismissLoader(isLoading = loading)
            }
            vm.errorMsg?.observe(this) { msg ->
                if (msg != null && msg != "") {
                    LogUtils.error("ViewModel Error--->$msg")
                }
            }
            vm.userMessages?.observe(this@ChatActivity) {
                try {
                    val model = Gson().fromJson(it.toString(), UserMessagesModel::class.java)
                    model?.data?.let { responseList ->
                        val dataRev = responseList.asReversed()
                        this.list.clear()
                        dataRev.forEachIndexed { _, chatMessageApiModal ->
                            val viewType: Int
                            when (chatMessageApiModal.messageType) {
                                1 -> {
                                    viewType = if (chatMessageApiModal.senderId.toString() == Singleton.instance?.userId) {
                                            Constants.ChatViewType.TEXT_MESSAGE_SELF
                                        } else {
                                            Constants.ChatViewType.TEXT_MESSAGE
                                        }
                                    list.add(
                                        ChatMessageModel(
                                            user_id = chatMessageApiModal.receiverId.toString(),
                                            messageId = chatMessageApiModal.messageId!!,
                                            message_date = DateTimeUtils.getDateTimeFromMillis(inputFormat = DateTimeUtils.timeFormat, milliSeconds= chatMessageApiModal.timestamp!!),
                                            messageStatus = if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver.status.toString(),
                                            viewType = viewType,
                                            message_model = MessageModel(messageString = chatMessageApiModal.messageContent),
                                            senderProfileImage = "",
                                        )
                                    )
                                }
                                2 -> {
                                    viewType =
                                        if (chatMessageApiModal.senderId.toString() == Singleton.instance?.userId
                                        ) {
                                            Constants.ChatViewType.PICTURE_MESSAGE_SELF
                                        } else {
                                            Constants.ChatViewType.PICTURE_MESSAGE
                                        }
                                    list.add(
                                        ChatMessageModel(
                                            user_id = chatMessageApiModal.receiverId.toString(),
                                            messageId = chatMessageApiModal.messageId!!,
                                            message_date = DateTimeUtils.getDateTimeFromMillis(inputFormat = DateTimeUtils.timeFormat,milliSeconds= chatMessageApiModal.timestamp!!),
                                            messageStatus = if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver.status.toString(),
                                            viewType = viewType,
                                            message_model = MessageModel(messageString = chatMessageApiModal.messageContent, imageUri = chatMessageApiModal.metadata?.url),
                                            senderProfileImage = "",
                                        )
                                    )
                                }
                            }
                        }
                        lifecycleScope.launch(Dispatchers.Main) {
                            notifyAdapterAndScrollToEnd()
                        }
                    }
                } catch (e: Exception) {
                    vm.errorMsg?.value = e.message
                }
            }
        }




        pushFragment(
            containerId = binding!!.footerLay.id,
            fragment = ChatControllerFragment(conversation = conversation, listener = object : ChatControllerListener {
                    override fun msgSendListener(modal: MessageReceiveModal, self: Boolean) {
                        viewModel?.prepareMessageForList(modal = modal, self = self)?.let {
                            list.add(it)
                            notifyAdapterAndScrollToEnd()
                        }
                    }

                    override fun galleryPickerListener() {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        resultLauncher.launch(intent)
                    }

                    override fun voiceNoteListener() {
                    }
                })
        )

        binding!!.btnBack.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding!!.btnBack.id -> {
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapterAndScrollToEnd() {
        chatAdapter!!.notifyDataSetChanged()
        if (list.size > 0) {
            binding!!.recyclerChat.smoothScrollToPosition(list.size - 1)
        }
    }

    private fun addMessageInList(modal: MessageReceiveModal, self: Boolean) {
        when (modal.messageType) {
            1 -> {
                val data = Gson().toJson(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self)
                            Constants.ChatViewType.TEXT_MESSAGE_SELF
                        else
                            Constants.ChatViewType.TEXT_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
                LogUtils.debug("Send Message-->", data)
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self)
                            Constants.ChatViewType.TEXT_MESSAGE_SELF
                        else
                            Constants.ChatViewType.TEXT_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
            2 -> {
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.PICTURE_MESSAGE_SELF else Constants.ChatViewType.PICTURE_MESSAGE,
                        message_model = MessageModel(
                            messageString = modal.messageContent,
                            imageUri = modal.metadata?.url,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
            3 -> {
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.AUDIO_MESSAGE_SELF else Constants.ChatViewType.AUDIO_MESSAGE,
                        message_model = MessageModel(
                            audioFileUri = modal.metadata?.url,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
            4 -> {
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.VIDEO_MESSAGE_SELF else Constants.ChatViewType.VIDEO_MESSAGE,
                        message_model = MessageModel(
                            imageUri = modal.metadata?.previewUrl,
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
            5 -> {
                //document
            }
            6 -> {
                //contact
            }
            7 -> {
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.LOCATION_MESSAGE_SELF else Constants.ChatViewType.LOCATION_MESSAGE,
                        message_model = MessageModel(
                            location = LocationMessageModel(
                                latLng = LatLng(
                                    modal.metadata?.lat?.toDouble()!!,
                                    modal.metadata?.long?.toDouble()!!
                                ),
                                locationName = modal.metadata?.addressTitle,
                                locationDescription = modal.metadata?.address
                            ),
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
            8 -> {
                //action
            }
            9 -> {
                list.add(
                    ChatMessageModel(
                        user_id = modal.receiverId,
                        messageId = modal.messageId,
                        message_date = DateTimeUtils.getDateTimeFromFormat(modal.timestamp.toLong()),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.TRANSACTION_SELF else Constants.ChatViewType.TRANSACTION,
                        message_model = MessageModel(
                            payment = PaymentModel(
                                transactionId = modal.metadata?.transactionId,
                                amount = modal.metadata?.amount
                            ),
                            mediaIdentifier = modal.mediaIdentifier
                        )
                    )
                )
            }
        }
    }


    private fun subscribeForConversation() {

        SocketTasks.subscribeReceiveMessage(this)
        //SocketTask.subscribeSendMessage(this)
        SocketTasks.subscribeMessageDelivered(this)
        SocketTasks.subscribeMessageRead(this)
        SocketTasks.subscribeMessageDelete(this)
        SocketTasks.subscribeBlockUser(this)
        SocketTasks.subscribeUnblockUser(this)
        SocketTasks.subscribeTypingEvent(this)
        SocketTasks.subscribeSendMessageAck(this)
        SocketTasks.subscribeConversationGet(this)
        SocketTasks.subscribeGetChatUserLastSeen(this)
        SocketTasks.subscribeMuteUnMute(this)
//        if (!chatContact.blocked) {
//            SocketTask.publishUpdateChatUserLastSeen(true)
//        }

    }

    private fun subscribeForChatHome() {
        SocketTasks.subscribeConversationGet(this)
        SocketTasks.subscribeReceiveMessage(this)
        SocketTasks.subscribeMuteUnMute(this)
    }


    override fun onReceiveMessage(modal: MessageReceiveModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            addMessageInList(modal, false)
            notifyAdapterAndScrollToEnd()
            val messageReadInputModal = MessageReadInputModal(
                Data = MessageReadInputModal.MessageReadInputDataModal(
                    messageId = modal.messageId,
                    senderId = modal.receiverId,
                    receiverId = modal.senderId
                ),
                userID = Constants.senderId
            )
            SocketTasks.publishMessageRead(messageReadInputModal)
        }

    }

    override fun onMessageDelivered(modal: MessageDeliveredModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            list.forEach {
                if (it.messageId == modal.messageId) {
                    it.messageStatus = ChatConstants.Chat.ChatMessageStatus.DELIVERED
                }
            }
            notifyAdapterAndScrollToEnd()
        }
    }

    override fun onMessageRead(modal: MessageReadOutputModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            list.forEach {
                if (it.messageId == modal.messageId) {
                    it.messageStatus = ChatConstants.Chat.ChatMessageStatus.READ
                }
            }
            notifyAdapterAndScrollToEnd()
        }
    }

    override fun onMessageDelete(modal: MessageDeleteOutputModal) {
    }

    override fun onBlockUser(modal: BlockUserOutputModal) {
    }

    override fun onUnBlockUser(modal: UnblockUserOutputModal) {
    }

    override fun onMuteUnMute(modal: MuteUnmuteOutputModal) {}

    @SuppressLint("SetTextI18n")
    override fun onTypingEvent(modal: TypingEventModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding!!.status.visibility = View.VISIBLE
            if (modal.action == "start") {
                binding!!.status.text = "Typing..."
            } else {
                binding!!.status.text = if (onlineStatus) "Online" else "Offline"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onGetLastSeen(modal: LastSeenModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding!!.status.visibility = View.VISIBLE
            onlineStatus = modal.onlineStatus
            if (modal.onlineStatus) {
                binding!!.status.text = "Online"
            } else {
                binding!!.status.text = "Offline"
            }
        }
    }

    override fun onSendMessageAck(modal: MessageReceiveModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            var verify = true
            list.forEachIndexed { index, chatMessageModel ->
                if (chatMessageModel.message_model?.mediaIdentifier == modal.mediaIdentifier) {
                    verify = false
                    list[index].messageId = modal.messageId
                    list[index].messageStatus = modal.receiver.status.toString()
                }
            }
            if (verify) {
                addMessageInList(modal, true)
            }
            notifyAdapterAndScrollToEnd()
        }
    }

    override fun onConversationGet(modal: ChatContact) {
    }

    private fun getMimeType(file: File): String {
        var type: String?
        val url = file.toString()
        val extension: String = MimeTypeMap.getFileExtensionFromUrl(url)
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        if (type == null) {
            type = "image/*"
        }
        return type
    }
}