package com.xint.example.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xint.chatlibrary.models.MessageReceiveModal
import com.xint.chatlibrary.models.MessageSendDataModal
import com.xint.chatlibrary.models.MessageSendModal
import com.xint.chatlibrary.models.TypingEventInputModal
import com.xint.chatlibrary.services.SocketTasks
import com.xint.chatlibrary.utils.ChatConstants
import com.xint.example.R
import com.xint.example.databinding.ChatFooterBinding
import com.xint.example.listeners.ChatControllerListener
import com.xint.example.utils.AppUtils
import com.xint.example.utils.Constants

class ChatControllerFragment(
    private var listener: ChatControllerListener
) : Fragment(),
    View.OnClickListener {

    private var binding: ChatFooterBinding? = null

    private val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            s?.let {

                binding!!.ivMic.visibility = if (s.isNotBlank()) View.GONE else View.VISIBLE
                binding!!.ivGallery.visibility =
                    if (s.isNotBlank()) View.GONE else View.VISIBLE
                binding!!.sendMsg.visibility =
                    if (s.isNotBlank()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFooterBinding.inflate(layoutInflater)
        init()
        return binding!!.root
    }

    private fun init() {
        binding!!.etText.addTextChangedListener(watcher)
        binding!!.etText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding!!.editText.setBackgroundResource(R.drawable.chat_footer_active_border)
            } else {
                AppUtils.updateView(
                    binding!!.editText,
                    binding!!.etText.toString()
                )
            }
        }

        binding!!.sendMsg.setOnClickListener(this)
        binding!!.ivGallery.setOnClickListener(this)
        binding!!.ivMic.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding!!.sendMsg.id -> {
//                lifecycleScope.launch(Dispatchers.Main) {
                val modal = MessageSendModal(
                    data = MessageSendDataModal(
                        chatType = ChatConstants.Chat.ChatType.SINGLE_CHAT,
                        conversationId = "",
                        groupId = "",
                        messageType = ChatConstants.Chat.ChatMessageType.TEXT,
                        mediaIdentifier = System.currentTimeMillis().toString(),
                        messageContent = binding!!.etText.text.toString().trim(),
                        receiverId = Constants.receiverId,
                        metadata = MessageSendDataModal.Metadata()
                    ),
                    userID = Constants.senderId,
                )
                SocketTasks.publishSendMessage(modal)
                SocketTasks.publishTypingEvent(
                    TypingEventInputModal(
                        TypingEventInputModal.TypingEventInputDataModal(
                            conversationId = "",
                            receiverId = Constants.receiverId,
                            action = "stop"
                        ),
                        userID = Constants.senderId,
                    )
                )

                val m1 = MessageReceiveModal(
                    chatType = ChatConstants.Chat.ChatType.SINGLE_CHAT,
                    messageContent = binding!!.etText.text.toString().trim(),
                    messageId = "0",
                    messageType = ChatConstants.Chat.ChatMessageType.TEXT,
                    receiverId = Constants.receiverId,
                    senderId = Constants.senderId,
                    conversationId = "",
                    timestamp = modal.data.mediaIdentifier,
                    mediaIdentifier = modal.data.mediaIdentifier,
                    status = 0,
                    receiver = MessageReceiveModal.ReceiverStatus(0),
                    metadata = MessageSendDataModal.Metadata()
                )
                listener.msgSendListener(modal = m1, self = true)
                binding!!.etText.setText("")

//                }
            }
            binding!!.ivGallery.id -> {
                listener.galleryPickerListener()
            }
            binding!!.ivMic.id -> {
//                listener.voiceNoteListener()
            }
        }
    }
}