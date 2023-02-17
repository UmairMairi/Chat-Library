package com.xint.example.listeners

import com.xint.chatlibrary.models.MessageReceiveModal

interface ChatControllerListener {
    fun msgSendListener(modal: MessageReceiveModal, self: Boolean)
    fun galleryPickerListener()
    fun voiceNoteListener()
}
