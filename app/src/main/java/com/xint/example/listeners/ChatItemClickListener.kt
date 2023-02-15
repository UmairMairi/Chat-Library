package com.xint.example.listeners

import android.view.View

interface ChatItemClickListener {
    fun onLongClick(view: View, pos: Int)
    fun onImageClick(view: View, pos: Int)
    fun onAudioClick(view: View, pos: Int)
    fun onVideoClick(view: View, pos: Int)
    fun onLocationClick(view: View, pos: Int)
    fun onPaymentClick(view: View, pos: Int)
}