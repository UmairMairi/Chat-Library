package com.xint.example.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.xint.chatlibrary.listeners.ChatListener
import com.xint.chatlibrary.models.*
import com.xint.example.adapters.ChatAdapter
import com.xint.example.databinding.ActivityChatBinding
import com.xint.example.utils.LogUtils
import com.xint.example.viewmodels.ChatActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ChatActivity : BaseActivity(), View.OnClickListener, ChatListener {
    private var binding: ActivityChatBinding? = null
    private var chatAdapter: ChatAdapter? = null
    private var viewModel: ChatActivityViewModel? = null
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

                        val file = MyUtils.getFile(
                            context = this@ChatActivity,
                            uri = uri
                        )
                        file.let { doc ->
                            filePreviewLauncher.launch(
                                Intent(
                                    this@ChatActivity,
                                    ImageVideoPreviewActivity::class.java
                                ).putExtras(
                                    bundleOf(
                                        Pair(
                                            Constants.ChatViewType.IMAGE_PREVIEW_PATH,
                                            doc.path
                                        ),
                                        Pair(
                                            Constants.ChatViewType.FILE_TYPE,
                                            Constants.ChatFileType.IMAGE
                                        )
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
//                val fileType = data?.getStringExtra(Constants.ChatViewType.FILE_TYPE)
                fileTextData = data?.getStringExtra(Constants.ChatViewType.BODY).toString()
                LogUtils.debug("File_path", filePath)
                fileStore = filePath?.let { File(it) }!!
                mimeType = getMimeType(fileStore)
                Log.e("mimeType", mimeType)
                if (mimeType.contains("video")) {
//                    val bitmap = retrieveThumbnailFrameFromVideo(fileStore.absolutePath)
//                    thumbFileStore = Utils.bitmapToFile(this, bitmap!!, "loopTest.jpeg")!!
//                    thumbFileStore = Compressor(this).compressToFile(thumbFileStore)
//                    presenter.callGetPreSignedUrlApi(
//                        fileStore.name,
//                        thumbFileStore?.name!!,
//                        System.currentTimeMillis().toString()
//                    )
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        fileStore = Compressor.compress(this@ChatActivity, fileStore)
                    }
//                    viewModel?.getPreSignedUrl(
//                        fileName = fileStore.name,
//                        thumbnail = getString(R.string.image),
//                        mediaIdentifier = System.currentTimeMillis().toString()

//                    )
                    viewModel?.uploadMedia(fileStore)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    fun init() {
        viewModel = ViewModelProvider(this@ChatActivity)[ChatActivityViewModel::class.java]
        chatAdapter = ChatAdapter(this, list)
        binding!!.recyclerChat.adapter = chatAdapter

        binding!!.btnBack.setOnClickListener(this)

        if (intent.hasExtra(Constants.RideFlow.rideDetails)) {
            tripDetails = intent.getParcelableExtra(Constants.RideFlow.rideDetails)
            tripDetails?.let {
                if (GlobalState.getInstance().isDriver) {
                    it.data?.riderInfo?.profileImage?.let { img ->
                        binding!!.profilePlaceholder.visibility = View.GONE
                        binding!!.userProfile.visibility = View.VISIBLE

                        Glide.with(this).load(img)
                            .placeholder(R.drawable.app_logo)
                            .into(binding!!.userProfile)
                    }
                    binding!!.title.text = splitName(it.data?.riderInfo?.name ?: "")
                    var name = it.data?.riderInfo?.name
                    if (MyUtils.isArabicLanguage()) {
                        name = it.data?.riderInfo?.arabicName ?: ""
                    }
                    binding!!.title.text = splitName(name ?: "")

                    viewModel?.getChatHistory(userId = it.data?.riderInfo?.id ?: "")
                } else {
                    it.data?.driverInfo?.profileImage?.let { img ->
                        binding!!.profilePlaceholder.visibility = View.GONE
                        binding!!.userProfile.visibility = View.VISIBLE

                        Glide.with(this).load(img)
                            .placeholder(R.drawable.app_logo)
                            .into(binding!!.userProfile)
                    }
                    binding!!.title.text = splitName(it.data?.driverInfo?.name ?: "")
                    var name = it.data?.driverInfo?.name
                    if (MyUtils.isArabicLanguage()) {
                        name = it.data?.driverInfo?.arabicName ?: ""
                    }
                    binding!!.title.text = splitName(name ?: "")
                    viewModel?.getChatHistory(userId = it.data?.driverInfo?.id ?: "")
                }
            }

            initBroadcasts()
            subscribeForConversation()

        }

        viewModel?.let { vm ->
            vm.chatHistoryResponse?.observe(this@ChatActivity) { json ->
                json?.let {
                    val model = Gson().fromJson(json.toString(), ChatMessageApiModal::class.java)
                    model?.data?.let { responseList ->
                        val dataRev = responseList.asReversed()
                        this.list.clear()
                        dataRev.forEachIndexed { _, chatMessageApiModal ->
                            val viewType: Int
                            when (chatMessageApiModal.messageType) {
                                1 -> {
                                    viewType =
                                        if (chatMessageApiModal.senderId.toString() == GlobalState.getInstance().userId) {
                                            Constants.ChatViewType.TEXT_MESSAGE_SELF

                                        } else {
                                            Constants.ChatViewType.TEXT_MESSAGE
                                        }
//                                    if (viewType == Constants.ChatViewType.TEXT_MESSAGE_SELF) {
//                                        if (chatMessageApiModal.receiver?.status.toString() != Constants.ChatMessageStatus.READ) {
//                                            val messageReadInputModal = MessageReadInputModal(
//                                                MessageReadInputModal.MessageReadInputDataModal(
//                                                    messageId = chatMessageApiModal.messageId,
//                                                    senderId = chatMessageApiModal.receiverId.toString(),
//                                                    receiverId = chatMessageApiModal.senderId.toString()
//                                                ), GlobalState.getInstance().userId
//                                            )
//                                            SocketTask.publishMessageRead(messageReadInputModal)
//                                        }
//                                    }
                                    list.add(
                                        ChatMessageModel(
                                            user_id = chatMessageApiModal.receiverId.toString(),
                                            messageId = chatMessageApiModal.messageId,
                                            message_date = DateTimeUtils.getDateTimeForChat(
                                                DateTimeUtils.getDateTimeForChat(
                                                    chatMessageApiModal.timestamp
                                                )
                                            ).toString(),
                                            messageStatus = if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                            viewType = viewType,
                                            message_model = MessageModel(messageString = chatMessageApiModal.messageContent),
                                            senderProfileImage = "",
                                        )
                                    )
                                }
                                2 -> {
                                    viewType =
                                        if (chatMessageApiModal.senderId.toString() == GlobalState.getInstance().userId
                                        ) {
                                            Constants.ChatViewType.PICTURE_MESSAGE_SELF
                                        } else {
                                            Constants.ChatViewType.PICTURE_MESSAGE
                                        }
                                    list.add(
                                        ChatMessageModel(
                                            user_id = chatMessageApiModal.receiverId.toString(),
                                            messageId = chatMessageApiModal.messageId,
                                            message_date = DateTimeUtils.getDateTimeForChat(
                                                DateTimeUtils.getDateTimeForChat(chatMessageApiModal.timestamp)
                                            ).toString(),
                                            messageStatus = if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                            viewType = viewType,
                                            message_model = MessageModel(
                                                messageString = chatMessageApiModal.messageContent,
                                                imageUri = chatMessageApiModal.metadata?.url
                                            ),
                                            senderProfileImage = "",
                                        )
                                    )
                                }
                                /*
                                 3 -> {
                                     if (chatMessageApiModal.senderId
                                         == LoopApp.getUser()?.customerId
                                     ) {
                                         viewType = Constants.ChatViewType.AUDIO_MESSAGE_SELF
                                     } else {
                                         viewType = Constants.ChatViewType.AUDIO_MESSAGE
                                     }
                                     chatList.add(
                                         ChatMessageModel(
                                             chatContact.receiver?.userId.toString(),
                                             chatMessageApiModal.messageId,
                                             DateTimeUtils.getDateTimeForChat(
                                                 DateTimeUtils.getDateTimeForChat(chatMessageApiModal.timestamp)
                                             ).toString(),
                                             if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                             chatContact.receiver?.profileImage!!,
                                             viewType,
                                             MessageModel(
                                                 messageString = fileTextData,
                                                 audioFileUri = chatMessageApiModal.metadata?.url
                                             )
                                         )
                                     )
                                 }
                                 4 -> {
                                     if (chatMessageApiModal.senderId
                                         == LoopApp.getUser()?.customerId
                                     ) {
                                         viewType = Constants.ChatViewType.VIDEO_MESSAGE_SELF
                                     } else {
                                         viewType = Constants.ChatViewType.VIDEO_MESSAGE
                                     }
                                     chatList.add(
                                         ChatMessageModel(
                                             chatContact.receiver?.userId.toString(),
                                             chatMessageApiModal.messageId,
                                             DateTimeUtils.getDateTimeForChat(
                                                 DateTimeUtils.getDateTimeForChat(chatMessageApiModal.timestamp)
                                             ).toString(),
                                             if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                             chatContact.receiver?.profileImage!!,
                                             viewType,
                                             MessageModel(
                                                 messageString = chatMessageApiModal.messageContent,
                                                 videoUri = chatMessageApiModal.metadata?.url,
                                                 videoPreviewUri = chatMessageApiModal.metadata?.previewUrl
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
                                     if (chatMessageApiModal.senderId
                                         == LoopApp.getUser()?.customerId
                                     ) {
                                         viewType = Constants.ChatViewType.LOCATION_MESSAGE_SELF
                                     } else {
                                         viewType = Constants.ChatViewType.LOCATION_MESSAGE
                                     }
                                     chatList.add(
                                         ChatMessageModel(
                                             chatContact.receiver?.userId.toString(),
                                             chatMessageApiModal.messageId,
                                             DateTimeUtils.getDateTimeForChat(
                                                 DateTimeUtils.getDateTimeForChat(chatMessageApiModal.timestamp)
                                             ).toString(),
                                             if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                             chatContact.receiver?.profileImage!!,
                                             viewType,
                                             MessageModel(
                                                 location = LocationMessageModel(
                                                     latLng = LatLng(
                                                         chatMessageApiModal.metadata?.lat?.toDouble()!!,
                                                         chatMessageApiModal.metadata?.long?.toDouble()!!
                                                     ),
                                                     locationName = chatMessageApiModal.metadata?.addressTitle,
                                                     locationDescription = chatMessageApiModal.metadata?.address
                                                 )
                                             )
                                         )
                                     )
                                 }
                                 8 -> {
                                     //action
                                 }
                                 9 -> {
                                     if (chatMessageApiModal.senderId
                                         == LoopApp.getUser()?.customerId
                                     ) {
                                         viewType = Constants.ChatViewType.TRANSACTION_SELF
                                     } else {
                                         viewType = Constants.ChatViewType.TRANSACTION
                                     }
                                     chatList.add(
                                         ChatMessageModel(
                                             chatContact.receiver?.userId.toString(),
                                             chatMessageApiModal.messageId,
                                             DateTimeUtils.getDateTimeForChat(
                                                 DateTimeUtils.getDateTimeForChat(chatMessageApiModal.timestamp)
                                             ).toString(),
                                             if (chatMessageApiModal.receiver == null) "0" else chatMessageApiModal.receiver?.status.toString(),
                                             chatContact.receiver?.profileImage!!,
                                             viewType,
                                             MessageModel(
                                                 payment = PaymentModel(
                                                     transactionId = chatMessageApiModal.metadata?.transactionId,
                                                     amount = chatMessageApiModal.metadata?.amount
                                                 )
                                             )
                                         )
                                     )
                                 }*/
                            }
                        }
                        lifecycleScope.launch(Dispatchers.Main) {
                            notifyAdapterAndScrollToEnd()
                        }
                        if (model.data.isNotEmpty()) {
                            vm.markAllRead(conversationId = model.data.first().conversationId)
                        }
                    }
                }
            }
            vm.markAllReadResponse?.observe(this@ChatActivity) { json ->
                json?.let { resp ->
                }
            }
            vm.uploadFileResponse?.observe(this@ChatActivity) { json ->
                json?.let { resp ->
                    val model = Gson().fromJson(resp.toString(), UploadMediaModel::class.java)
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (mimeType) {
                            Constants.ChatMimeType.AUDIO -> {
                                list.add(
                                    ChatMessageModel(
                                        user_id = "",
                                        messageId = "",
                                        message_date = DateTimeUtils.getDateTimeForChat(
                                            DateTimeUtils.getDateTimeForChat(System.currentTimeMillis())
                                        ).toString(),
                                        messageStatus = "0",
                                        senderProfileImage = senderProfileImage,
                                        viewType = Constants.ChatViewType.AUDIO_MESSAGE_SELF,
                                        message_model = MessageModel(
                                            audioFileUri = model.data.mediaUrl,
//                                            mediaIdentifier = model.data.params.mediaIdentifier,
                                            messageString = fileTextData
                                        )
                                    )
                                )
                                notifyAdapterAndScrollToEnd()
//                                vm.uploadPreSignedUrl(file = fileStore, model.data.presignedUrl)

                            }
                            Constants.ChatMimeType.IMAGE,
                            Constants.ChatMimeType.IMAGE_PNG -> {
                                list.add(
                                    ChatMessageModel(
                                        user_id = "",
                                        messageId = "",
                                        message_date = DateTimeUtils.getDateTimeForChat(
                                            DateTimeUtils.getDateTimeForChat(System.currentTimeMillis())
                                        ).toString(),
                                        messageStatus = "0",
                                        senderProfileImage = senderProfileImage,
                                        viewType = Constants.ChatViewType.PICTURE_MESSAGE_SELF,
                                        message_model = MessageModel(
                                            imageUri = model.data.mediaUrl,
                                            mediaIdentifier = mimeType,
                                            messageString = fileTextData
                                        )
                                    )
                                )
                                notifyAdapterAndScrollToEnd()

                                val modal = MessageSendModal(
                                    MessageSendDataModal(
                                        chatType = Constants.ChatType.SINGLE_CHAT,
                                        conversationId = "",
                                        groupId = "",
                                        messageType =
                                        if (mimeType == Constants.ChatMimeType.IMAGE) Constants.ChatMessageType.IMAGE
                                        else if (mimeType == Constants.ChatMimeType.AUDIO) Constants.ChatMessageType.AUDIO
                                        else Constants.ChatMessageType.VIDEO,
                                        messageContent = fileTextData /*if (mimeType == Constants.ChatMimeType.IMAGE) "Image" else if (mimeType == Constants.ChatMimeType.AUDIO) "Audio" else "Video"*/,
                                        mediaIdentifier = mimeType,
                                        receiverId = if (GlobalState.getInstance().isDriver) tripDetails?.data?.riderId
                                            ?: "N/A" else tripDetails?.data?.driverId ?: "N/A",
                                        metadata = MessageSendDataModal.Metadata(
                                            mime = mimeType,
                                            url = model.data.mediaUrl,
//                                            previewUrl = model.data.thumbAccessUrl
                                        )
                                    ), GlobalState.getInstance().userId
                                )
                                SocketTask.publishSendMessage(modal)

                            }
                            else -> {
                                list.add(
                                    ChatMessageModel(
                                        user_id = "",
                                        messageId = "",
                                        message_date = DateTimeUtils.getDateTimeForChat(
                                            DateTimeUtils.getDateTimeForChat(System.currentTimeMillis())
                                        ).toString(),
                                        messageStatus = "0",
                                        senderProfileImage = senderProfileImage,
                                        viewType = Constants.ChatViewType.VIDEO_MESSAGE_SELF,
                                        message_model = MessageModel(
                                            videoUri = model.data.mediaUrl,
//                                            videoPreviewUri = model.data.thumbAccessUrl,
//                                            mediaIdentifier = model.data.params.mediaIdentifier,
                                            messageString = fileTextData
                                        )
                                    )
                                )
                                notifyAdapterAndScrollToEnd()
                                //                        presenter.callUploadThumbNailFilePreSignedUrlApi(file = thumbFileStore, model)
                                //                        presenter.callUploadFilePreSignedUrlApi(file = fileStore, model)
                            }
                        }
                    }
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(
                binding!!.footerLay.id,
                ChatControllerFragment(tripDetails = tripDetails,
                    object : ChatControllerListener {
                        override fun msgSendListener(modal: MessageReceiveModal, self: Boolean) {
                            addMessageInList(modal, self)
                            notifyAdapterAndScrollToEnd()
                        }

                        override fun galleryPickerListener() {
                            pickImageFromGallery()

                        }

                        override fun voiceNoteListener() {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(
                                        this@ChatActivity, Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    supportFragmentManager.beginTransaction()
                                        .setCustomAnimations(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left,
                                            R.anim.slide_in_left,
                                            R.anim.slide_out_right
                                        )
                                        .replace(binding!!.footerLay.id, ChatFooterVoiceFragment(
                                            listener = object : AudioRecordListener {
                                                override fun onAudioRecordSuccess(filePath: String) {
                                                    LogUtils.debug(
                                                        "Record FilePath -->",
                                                        filePath
                                                    )
                                                    fileStore = File(filePath)
                                                    mimeType = getMimeType(fileStore)
//                                                    viewModel?.getPreSignedUrl(fileStore.name, getString(R.string.audio), System.currentTimeMillis().toString() )
                                                }
                                            }
                                        ))
                                        .addToBackStack(Constants.RideFlow.fragmentStack)
                                        .commit()
                                } else {
                                    checkAudioPermission()
                                }
                            } else {
                                supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                    )
                                    .replace(binding!!.footerLay.id, ChatFooterVoiceFragment(
                                        listener = object : AudioRecordListener {
                                            override fun onAudioRecordSuccess(filePath: String) {
                                                LogUtils.debug("Record FilePath -->", filePath)
                                                fileStore = File(filePath)
                                                mimeType = getMimeType(fileStore)
//                                                viewModel?.getPreSignedUrl(fileStore.name, getString(R.string.audio), System.currentTimeMillis().toString())
                                            }
                                        }
                                    ))
                                    .addToBackStack(Constants.RideFlow.fragmentStack)
                                    .commit()
                            }
                        }
                    }
                )
            )
            .commit()
    }

    private fun checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
            ),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding!!.btnBack.id -> {
                onBackPressed()
            }
        }
    }

    private fun splitName(name: String): String {
        val list: List<String> = name.split(" ")
        return list.first()
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
                        messageStatus = modal.receiver.status.toString(),
                        senderProfileImage = senderProfileImage,
                        viewType = if (self) Constants.ChatViewType.LOCATION_MESSAGE_SELF else Constants.ChatViewType.LOCATION_MESSAGE,
                        message_model = MessageModel(
                            location = LocationMessageModel(
                                latLng = LatLng(
                                    modal.metadata?.lat?.toDouble()!!,
                                    modal.metadata.long?.toDouble()!!
                                ),
                                locationName = modal.metadata.addressTitle,
                                locationDescription = modal.metadata.address
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
                        message_date = DateTimeUtils.getDateTimeForChat(
                            DateTimeUtils.getDateTimeForChat(modal.timestamp.toLong())
                        ).toString(),
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


    private fun initBroadcasts() {
        val intentFilters = IntentFilter()
        intentFilters.addAction(Constants.SOCKET_TASK)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            socketBroadcastManager,
            intentFilters
        )
    }

    private val socketBroadcastManager: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            intent.action?.let {
                if (it == Constants.SOCKET_TASK) {
                    subscribeForConversation()
                    Log.e("SocketBroadCast", "EndAtConversation")
                }
            }
        }
    }

    private fun subscribeForConversation() {

        SocketTask.subscribeReceiveMessage(this)
        //SocketTask.subscribeSendMessage(this)
        SocketTask.subscribeMessageDelivered(this)
        SocketTask.subscribeMessageRead(this)
        SocketTask.subscribeMessageDelete(this)
        SocketTask.subscribeBlockUser(this)
        SocketTask.subscribeUnblockUser(this)
        SocketTask.subscribeTypingEvent(this)
        SocketTask.subscribeSendMessageAck(this)
        SocketTask.subscribeConversationGet(this)
        SocketTask.subscribeGetChatUserLastSeen(this)
        SocketTask.subscribeMuteUnMute(this)
//        if (!chatContact.blocked) {
//            SocketTask.publishUpdateChatUserLastSeen(true)
//        }

    }

    private fun subscribeForChatHome() {
        SocketTask.subscribeConversationGet(this)
        SocketTask.subscribeReceiveMessage(this)
        SocketTask.subscribeMuteUnMute(this)
    }


    override fun onReceiveMessage(modal: MessageReceiveModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            addMessageInList(modal, false)
            notifyAdapterAndScrollToEnd()
            val messageReadInputModal = MessageReadInputModal(
                MessageReadInputModal.MessageReadInputDataModal(
                    messageId = modal.messageId,
                    senderId = modal.receiverId,
                    receiverId = modal.senderId
                ), GlobalState.getInstance().userId
            )
            SocketTask.publishMessageRead(messageReadInputModal)
        }

    }

    override fun onMessageDelivered(modal: MessageDeliveredModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            list.forEach {
                if (it.messageId == modal.messageId) {
                    it.messageStatus = Constants.ChatMessageStatus.DELIVERED
                }
            }
            notifyAdapterAndScrollToEnd()
        }
    }

    override fun onMessageRead(modal: MessageReadOutputModal) {
        lifecycleScope.launch(Dispatchers.Main) {
            list.forEach {
                if (it.messageId == modal.messageId) {
                    it.messageStatus = Constants.ChatMessageStatus.READ
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
                binding!!.status.text = getString(R.string.online)
            } else {
                binding!!.status.text = getString(R.string.offline)
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

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
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


    interface ChatControllerListener {
        fun msgSendListener(modal: MessageReceiveModal, self: Boolean)
        fun galleryPickerListener()
        fun voiceNoteListener()
    }

    interface AudioRecordListener {
        fun onAudioRecordSuccess(filePath: String)
    }
}