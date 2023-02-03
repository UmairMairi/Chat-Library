package com.xint.chatlibrary.listeners

import com.xint.chatlibrary.models.*

interface ChatListener {
//    fun onGetSendMessage(modal: MessageReceiveModal)
    fun onReceiveMessage(modal: MessageReceiveModal)
    fun onMessageDelivered(modal: MessageDeliveredModal)
    fun onMessageRead(modal: MessageReadOutputModal)
    fun onMessageDelete(modal: MessageDeleteOutputModal)
    fun onBlockUser(modal: BlockUserOutputModal)
    fun onUnBlockUser(modal: UnblockUserOutputModal)
    fun onMuteUnMute(modal: MuteUnmuteOutputModal)
    fun onTypingEvent(modal: TypingEventModal)
    fun onGetLastSeen(modal: LastSeenModal)
    fun onSendMessageAck(modal: MessageReceiveModal)
    fun onConversationGet(modal: ChatContact)
}