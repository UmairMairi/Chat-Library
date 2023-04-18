package com.xint.chatfooter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ChatFooterView {
    enum class UserBehaviour {
        CANCELING, LOCKING, NONE
    }

    enum class RecordingBehaviour {
        CANCELED, LOCKED, LOCK_DONE, RELEASED
    }

    interface RecordingListener {
        fun onRecordingStarted()
        fun onRecordingLocked()
        fun onRecordingCompleted()
        fun onRecordingCanceled()
    }


    private var viewContainer: LinearLayout? = null
    private  var layoutAttachmentOptions: LinearLayout? = null
    private var imageViewAudio: View? = null
    private  var imageViewLockArrow: View? = null
    private  var imageViewLock: View? = null
    private  var imageViewMic: View? = null
    private  var dustin: View? = null
    private  var dustin_cover: View? = null
    private  var imageViewStop: View? = null
    private  var imageViewSend: View? = null
    private var layoutAttachment: View? = null
    private  var layoutDustin: View? = null
    private  var imageViewAttachment: View? = null
    private  var btnGallery: View? = null
    private var layoutSlideCancel: View? = null
    private  var layoutLock : View? = null
    private var editTextMessage: EditText? = null
    private var timeText: TextView? = null
    private  var textViewSlide: TextView? = null

    private var stop: ImageView? = null
    private  var audio: ImageView? = null
    private  var send: ImageView? = null

    private var animBlink: Animation? = null
    private  var animJump: Animation? = null
    private  var animJumpFast: Animation? = null

    private val isDeleting = false
    private val stopTrackingAction = false
    private var handler: Handler? = null

    private val audioTotalTime = 0
    private val timerTask: TimerTask? = null
    private val audioTimer: Timer? = null
    private var timeFormatter: SimpleDateFormat? = null

    private val lastX = 0f
    private  var lastY: Float = 0f
    private val firstX = 0f
    private  var firstY: Float = 0f

    private val directionOffset = 0f
    private  var cancelOffset: Float = 0f
    private  var lockOffset: Float = 0f
    private var dp = 0f
    private val isLocked = false

    private val userBehaviour = AudioRecordView.UserBehaviour.NONE
    private val recordingListener: AudioRecordView.RecordingListener? = null

    var isLayoutDirectionRightToLeft = false

    var screenWidth = 0
    var screenHeight: Int = 0

    private val attachmentOptionList: List<AttachmentOption>? = null
    private val attachmentOptionsListener: AttachmentOptionsListener? = null

    private val layoutAttachments: List<LinearLayout>? = null

    private var context: Context? = null

    private val showCameraIcon = true
    private  var showAttachmentIcon: Boolean = true
    private  var showEmojiIcon: Boolean = true
    private val removeAttachmentOptionAnimation = false


    fun initView(view: ViewGroup?) {
        if (view == null) {
            return
        }
        context = view.context
        view.removeAllViews()
        view.addView(LayoutInflater.from(view.context).inflate(R.layout.record_view, null))
        timeFormatter = SimpleDateFormat("m:ss", Locale.getDefault())
        val displayMetrics = view.context.resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        isLayoutDirectionRightToLeft = view.context.resources.getBoolean(R.bool.is_right_to_left)
        viewContainer = view.findViewById(R.id.layoutContainer)
        layoutAttachmentOptions = view.findViewById(R.id.layoutAttachmentOptions)
        imageViewAttachment = view.findViewById(R.id.imageViewAttachment)
        btnGallery = view.findViewById(R.id.iv_gallery)
        editTextMessage = view.findViewById(R.id.et_text)
        send = view.findViewById(R.id.imageSend)
        stop = view.findViewById(R.id.imageStop)
        audio = view.findViewById(R.id.imageAudio)
        imageViewAudio = view.findViewById(R.id.imageViewAudio)
        imageViewStop = view.findViewById(R.id.imageViewStop)
        imageViewSend = view.findViewById(R.id.imageViewSend)
        imageViewLock = view.findViewById(R.id.imageViewLock)
        imageViewLockArrow = view.findViewById(R.id.imageViewLockArrow)
        layoutDustin = view.findViewById(R.id.layoutDustin)
        layoutAttachment = view.findViewById(R.id.layoutAttachment)
        textViewSlide = view.findViewById(R.id.textViewSlide)
        timeText = view.findViewById(R.id.textViewTime)
        layoutSlideCancel = view.findViewById(R.id.layoutSlideCancel)
        //        layoutEffect2 = view.findViewById(R.id.layoutEffect2);
//        layoutEffect1 = view.findViewById(R.id.layoutEffect1);
        layoutLock = view.findViewById(R.id.layoutLock)
        imageViewMic = view.findViewById(R.id.imageViewMic)
        dustin = view.findViewById(R.id.dustin)
        dustin_cover = view.findViewById(R.id.dustin_cover)
        handler = Handler(Looper.getMainLooper())
        dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            view.context.resources.displayMetrics
        )
        animBlink = AnimationUtils.loadAnimation(
            view.context,
            R.anim.blink
        )
        animJump = AnimationUtils.loadAnimation(
            view.context,
            R.anim.jump
        )
        animJumpFast = AnimationUtils.loadAnimation(
            view.context,
            R.anim.jump_fast
        )
        setupRecording()
        setupAttachmentOptions()
    }

}