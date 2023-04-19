package com.xint.chatfooter

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.hypot

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

    private var isDeleting = false
    private var stopTrackingAction = false
    private var handler: Handler? = null

    private var audioTotalTime = 0
    private var timerTask: TimerTask? = null
    private var audioTimer: Timer? = null
    private var timeFormatter: SimpleDateFormat? = null

    private var lastX = 0f
    private  var lastY: Float = 0f
    private var firstX = 0f
    private  var firstY: Float = 0f

    private val directionOffset = 0f
    private  var cancelOffset: Float = 0f
    private  var lockOffset: Float = 0f
    private var dp = 0f
    private var isLocked = false

    private var userBehaviour = UserBehaviour.NONE
    private var recordingListener: RecordingListener? = null

    var isLayoutDirectionRightToLeft = false

    var screenWidth = 0
    var screenHeight: Int = 0

    private var attachmentOptionList: List<AttachmentOption>? = null
    private var attachmentOptionsListener: AttachmentOptionsListener? = null

    private var layoutAttachments: ArrayList<LinearLayout>? = null

    private var context: Context? = null

    private var showCameraIcon = true
    private  var showAttachmentIcon: Boolean = true
    private  var showEmojiIcon: Boolean = true
    private var removeAttachmentOptionAnimation = false


    @SuppressLint("InflateParams")
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


    fun changeSlideToCancelText(textResourceId: Int) {
        textViewSlide!!.setText(textResourceId)
    }

    fun isShowCameraIcon(): Boolean {
        return showCameraIcon
    }

    fun showCameraIcon(showCameraIcon: Boolean) {
        this.showCameraIcon = showCameraIcon
        if (showCameraIcon) {
            btnGallery!!.visibility = View.VISIBLE
        } else {
            btnGallery!!.visibility = View.GONE
        }
    }

    fun isShowAttachmentIcon(): Boolean {
        return showAttachmentIcon
    }

    fun showAttachmentIcon(showAttachmentIcon: Boolean) {
        this.showAttachmentIcon = showAttachmentIcon
        if (showAttachmentIcon) {
            imageViewAttachment!!.visibility = View.VISIBLE
        } else {
            imageViewAttachment!!.visibility = View.INVISIBLE
        }
    }

    fun setAttachmentOptions(
        attachmentOptionList: List<AttachmentOption>?,
        attachmentOptionsListener: AttachmentOptionsListener?
    ) {
        this.attachmentOptionList = attachmentOptionList
        this.attachmentOptionsListener = attachmentOptionsListener
        if (this.attachmentOptionList != null && this.attachmentOptionList!!.isNotEmpty()) {
            layoutAttachmentOptions!!.removeAllViews()
            var count = 0
            var linearLayoutMain: LinearLayout? = null
            layoutAttachments = ArrayList()
            for (attachmentOption in this.attachmentOptionList!!) {
                if (count == 6) {
                    break
                }
                if (count == 0 || count == 3) {
                    linearLayoutMain = LinearLayout(context)
                    linearLayoutMain.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    linearLayoutMain.orientation = LinearLayout.HORIZONTAL
                    linearLayoutMain.gravity = Gravity.CENTER
                    layoutAttachmentOptions!!.addView(linearLayoutMain)
                }
                val linearLayout = LinearLayout(context)
                linearLayout.layoutParams =
                    LinearLayout.LayoutParams(
                        (dp * 84).toInt(),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                linearLayout.setPadding(
                    (dp * 4).toInt(),
                    (dp * 12).toInt(),
                    (dp * 4).toInt(),
                    (dp * 0).toInt()
                )
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.gravity = Gravity.CENTER
                layoutAttachments!!.add(linearLayout)
                val imageView = ImageView(context)
                imageView.layoutParams =
                    LinearLayout.LayoutParams((dp * 48).toInt(), (dp * 48).toInt())
                imageView.setImageResource(attachmentOption.getResourceImage())
                val textView = TextView(context)
                TextViewCompat.setTextAppearance(textView, R.style.TextAttachmentOptions)
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textView.setPadding(
                    (dp * 4).toInt(),
                    (dp * 4).toInt(),
                    (dp * 4).toInt(),
                    (dp * 0).toInt()
                )
                textView.maxLines = 1
                textView.text = attachmentOption.getTitle()
                linearLayout.addView(imageView)
                linearLayout.addView(textView)
                linearLayoutMain!!.addView(linearLayout)
                linearLayout.setOnClickListener {
                    hideAttachmentOptionView()
                    this.attachmentOptionsListener?.onClick(attachmentOption)
                }
                count++
            }
        }
    }

    fun hideAttachmentOptionView() {
        if (layoutAttachment!!.visibility == View.VISIBLE) {
            imageViewAttachment!!.performClick()
        }
    }

    fun showAttachmentOptionView() {
        if (layoutAttachment!!.visibility != View.VISIBLE) {
            imageViewAttachment!!.performClick()
        }
    }

    private fun setupAttachmentOptions() {
        imageViewAttachment!!.setOnClickListener {
            if (layoutAttachment!!.visibility == View.VISIBLE) {
                val x =
                    if (isLayoutDirectionRightToLeft) (dp * (18 + 40 + 4 + 56)).toInt() else (screenWidth - dp * (18 + 40 + 4 + 56)).toInt()
                val y = (dp * 220).toInt()
                val startRadius = 0
                val endRadius = hypot(
                    (screenWidth - dp * (8 + 8)).toDouble(),
                    (dp * 220).toDouble()
                ).toInt()
                val anim = ViewAnimationUtils.createCircularReveal(
                    layoutAttachment,
                    x,
                    y,
                    endRadius.toFloat(),
                    startRadius.toFloat()
                )
                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        layoutAttachment!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                })
                anim.start()
            } else {
                if (!removeAttachmentOptionAnimation) {
                    var count = 0
                    if (layoutAttachments != null && layoutAttachments!!.isNotEmpty()) {
                        var arr = intArrayOf(5, 4, 2, 3, 1, 0)
                        if (isLayoutDirectionRightToLeft) {
                            arr = intArrayOf(3, 4, 0, 5, 1, 2)
                        }
                        for (i in layoutAttachments!!.indices) {
                            if (arr[i] < layoutAttachments!!.size) {
                                val layout = layoutAttachments!![arr[i]]
                                layout.scaleX = 0.4f
                                layout.alpha = 0f
                                layout.scaleY = 0.4f
                                layout.translationY = dp * 48 * 2
                                layout.visibility = View.INVISIBLE
                                layout.animate().scaleX(1f).scaleY(1f).alpha(1f).translationY(0f)
                                    .setStartDelay((count * 25 + 50).toLong()).setDuration(300)
                                    .setInterpolator(OvershootInterpolator())
                                    .start()
                                layout.visibility = View.VISIBLE
                                count++
                            }
                        }
                    }
                }
                val x =
                    if (isLayoutDirectionRightToLeft) (dp * (18 + 40 + 4 + 56)).toInt() else (screenWidth - dp * (18 + 40 + 4 + 56)).toInt()
                val y = (dp * 220).toInt()
                val startRadius = 0
                val endRadius = hypot(
                    (screenWidth - dp * (8 + 8)).toDouble(),
                    (dp * 220).toDouble()
                ).toInt()
                val anim = ViewAnimationUtils.createCircularReveal(
                    layoutAttachment,
                    x,
                    y,
                    startRadius.toFloat(),
                    endRadius.toFloat()
                )
                anim.duration = 500
                layoutAttachment!!.visibility = View.VISIBLE
                anim.start()
            }
        }
    }

    fun removeAttachmentOptionAnimation(removeAttachmentOptionAnimation: Boolean) {
        this.removeAttachmentOptionAnimation = removeAttachmentOptionAnimation
    }

    fun setContainerView(layoutResourceID: Int): View? {
        val view = LayoutInflater.from(viewContainer!!.context).inflate(layoutResourceID, null)
        if (view == null) {
            showErrorLog("Unable to create the Container View from the layoutResourceID")
            return null
        }
        viewContainer!!.removeAllViews()
        viewContainer!!.addView(view)
        return view
    }

    fun setAudioRecordButtonImage(imageResource: Int) {
        audio!!.setImageResource(imageResource)
    }

    fun setStopButtonImage(imageResource: Int) {
        stop!!.setImageResource(imageResource)
    }

    fun setSendButtonImage(imageResource: Int) {
        send!!.setImageResource(imageResource)
    }

    fun getRecordingListener(): RecordingListener? {
        return recordingListener
    }

    fun setRecordingListener(recordingListener: RecordingListener?) {
        this.recordingListener = recordingListener
    }

    fun getSendView(): View? {
        return imageViewSend
    }

    fun getAttachmentView(): View? {
        return imageViewAttachment
    }


    fun getCameraView(): View? {
        return btnGallery
    }

    fun getMessageView(): EditText? {
        return editTextMessage
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecording() {
        imageViewSend!!.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(
            LinearInterpolator()
        ).start()
        editTextMessage!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    if (imageViewSend!!.visibility != View.GONE) {
                        imageViewSend!!.visibility = View.GONE
                        imageViewSend!!.animate().scaleX(0f).scaleY(0f).setDuration(100)
                            .setInterpolator(
                                LinearInterpolator()
                            ).start()
                    }
                    if (showCameraIcon) {
                        if (btnGallery!!.visibility != View.VISIBLE && !isLocked) {
                            btnGallery!!.visibility = View.VISIBLE
                            btnGallery!!.animate().scaleX(1f).scaleY(1f).setDuration(100)
                                .setInterpolator(
                                    LinearInterpolator()
                                ).start()
                        }
                    }
                } else {
                    if (imageViewSend!!.visibility != View.VISIBLE && !isLocked) {
                        imageViewSend!!.visibility = View.VISIBLE
                        imageViewSend!!.animate().scaleX(1f).scaleY(1f).setDuration(100)
                            .setInterpolator(
                                LinearInterpolator()
                            ).start()
                    }
                    if (showCameraIcon) {
                        if (btnGallery!!.visibility != View.GONE) {
                            btnGallery!!.visibility = View.GONE
                            btnGallery!!.animate().scaleX(0f).scaleY(0f).setDuration(100)
                                .setInterpolator(
                                    LinearInterpolator()
                                ).start()
                        }
                    }
                }
            }
        })
        imageViewAudio!!.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (isDeleting) {
                return@OnTouchListener true
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                cancelOffset = (screenWidth / 2.8).toFloat()
                lockOffset = (screenWidth / 2.5).toFloat()
                if (firstX == 0f) {
                    firstX = motionEvent.rawX
                }
                if (firstY == 0f) {
                    firstY = motionEvent.rawY
                }
                startRecord()
            } else if (motionEvent.action == MotionEvent.ACTION_UP
                || motionEvent.action == MotionEvent.ACTION_CANCEL
            ) {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    stopRecording(RecordingBehaviour.RELEASED)
                }
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                if (stopTrackingAction) {
                    return@OnTouchListener true
                }
                var direction:UserBehaviour =
                    UserBehaviour.NONE
                val motionX = abs(firstX - motionEvent.rawX)
                val motionY = abs(firstY - motionEvent.rawY)
                if (if (isLayoutDirectionRightToLeft) motionX > directionOffset && lastX > firstX && lastY > firstY else motionX > directionOffset && lastX < firstX && lastY < firstY) {
                    if (if (isLayoutDirectionRightToLeft) motionX > motionY && lastX > firstX else motionX > motionY && lastX < firstX) {
                        direction = UserBehaviour.CANCELING
                    } else if (motionY > motionX && lastY < firstY) {
                        direction = UserBehaviour.LOCKING
                    }
                } else if (if (isLayoutDirectionRightToLeft) motionX > motionY && motionX > directionOffset && lastX > firstX else motionX > motionY && motionX > directionOffset && lastX < firstX) {
                    direction = UserBehaviour.CANCELING
                } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                    direction = UserBehaviour.LOCKING
                }
                if (direction == UserBehaviour.CANCELING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.rawY + imageViewAudio!!.width / 2 > firstY) {
                        userBehaviour = UserBehaviour.CANCELING
                    }
                    if (userBehaviour == UserBehaviour.CANCELING) {
                        translateX(-(firstX - motionEvent.rawX))
                    }
                } else if (direction == UserBehaviour.LOCKING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.rawX + imageViewAudio!!.width / 2 > firstX) {
                        userBehaviour = UserBehaviour.LOCKING
                    }
                    if (userBehaviour == UserBehaviour.LOCKING) {
                        translateY(-(firstY - motionEvent.rawY))
                    }
                }
                lastX = motionEvent.rawX
                lastY = motionEvent.rawY
            }
            view.onTouchEvent(motionEvent)
            true
        })
        imageViewStop!!.setOnClickListener {
            isLocked = false
            stopRecording(RecordingBehaviour.LOCK_DONE)
        }
    }

    private fun translateY(y: Float) {
        if (y < -lockOffset) {
            locked()
            imageViewAudio!!.translationY = 0f
            return
        }
        if (layoutLock!!.visibility != View.VISIBLE) {
            layoutLock!!.visibility = View.VISIBLE
        }
        imageViewAudio!!.translationY = y
        layoutLock!!.translationY = y / 2
        imageViewAudio!!.translationX = 0f
    }

    private fun translateX(x: Float) {
        if (if (isLayoutDirectionRightToLeft) x > cancelOffset else x < -cancelOffset) {
            canceled()
            imageViewAudio!!.translationX = 0f
            layoutSlideCancel!!.translationX = 0f
            return
        }
        imageViewAudio!!.translationX = x
        layoutSlideCancel!!.translationX = x
        layoutLock!!.translationY = 0f
        imageViewAudio!!.translationY = 0f
        if (Math.abs(x) < imageViewMic!!.width / 2) {
            if (layoutLock!!.visibility != View.VISIBLE) {
                layoutLock!!.visibility = View.VISIBLE
            }
        } else {
            if (layoutLock!!.visibility != View.GONE) {
                layoutLock!!.visibility = View.GONE
            }
        }
    }

    private fun locked() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.LOCKED)
        isLocked = true
    }

    private fun canceled() {
        stopTrackingAction = true
        stopRecording(RecordingBehaviour.CANCELED)
    }

    private fun stopRecording(recordingBehaviour: RecordingBehaviour) {
        stopTrackingAction = true
        firstX = 0f
        firstY = 0f
        lastX = 0f
        lastY = 0f
        userBehaviour = UserBehaviour.NONE
        imageViewAudio!!.animate().scaleX(1f).scaleY(1f).translationX(0f).translationY(0f)
            .setDuration(100).setInterpolator(
                LinearInterpolator()
            ).start()
        layoutSlideCancel!!.translationX = 0f
        layoutSlideCancel!!.visibility = View.GONE
        layoutLock!!.visibility = View.GONE
        layoutLock!!.translationY = 0f
        imageViewLockArrow!!.clearAnimation()
        imageViewLock!!.clearAnimation()
        if (isLocked) {
            return
        }

        when (recordingBehaviour) {
            RecordingBehaviour.LOCKED -> {
                imageViewStop!!.visibility = View.VISIBLE
                recordingListener?.onRecordingLocked()
            }
            RecordingBehaviour.CANCELED -> {
                timeText!!.clearAnimation()
                timeText!!.visibility = View.INVISIBLE
                imageViewMic!!.visibility = View.INVISIBLE
                imageViewStop!!.visibility = View.GONE
                timerTask!!.cancel()
                delete()
                recordingListener?.onRecordingCanceled()
            }
            RecordingBehaviour.RELEASED, RecordingBehaviour.LOCK_DONE -> {
                timeText!!.clearAnimation()
                timeText!!.visibility = View.INVISIBLE
                imageViewMic!!.visibility = View.INVISIBLE
                editTextMessage!!.visibility = View.VISIBLE
                if (showAttachmentIcon) {
                    imageViewAttachment!!.visibility = View.VISIBLE
                }
                if (showCameraIcon) {
                    btnGallery!!.visibility = View.VISIBLE
                }
                imageViewStop!!.visibility = View.GONE
                editTextMessage!!.requestFocus()
                timerTask!!.cancel()
                recordingListener?.onRecordingCompleted()
            }
        }
    }

    private fun startRecord() {
        recordingListener?.onRecordingStarted()
        hideAttachmentOptionView()
        stopTrackingAction = false
        editTextMessage!!.visibility = View.INVISIBLE
        imageViewAttachment!!.visibility = View.INVISIBLE
        btnGallery!!.visibility = View.INVISIBLE
        imageViewAudio!!.animate().scaleXBy(1f).scaleYBy(1f).setDuration(200).setInterpolator(
            OvershootInterpolator()
        ).start()
        timeText!!.visibility = View.VISIBLE
        layoutLock!!.visibility = View.VISIBLE
        layoutSlideCancel!!.visibility = View.VISIBLE
        imageViewMic!!.visibility = View.VISIBLE
        timeText!!.startAnimation(animBlink)
        imageViewLockArrow!!.clearAnimation()
        imageViewLock!!.clearAnimation()
        imageViewLockArrow!!.startAnimation(animJumpFast)
        imageViewLock!!.startAnimation(animJump)
        if (audioTimer == null) {
            audioTimer = Timer()
            timeFormatter!!.timeZone = TimeZone.getTimeZone("UTC")
        }
        timerTask = object : TimerTask() {
            override fun run() {
                handler!!.post {
                    timeText!!.text = timeFormatter!!.format(Date((audioTotalTime * 1000).toLong()))
                    audioTotalTime++
                }
            }
        }
        audioTotalTime = 0
        audioTimer?.schedule(timerTask, 0, 1000)
    }

    private fun delete() {
        imageViewMic!!.visibility = View.VISIBLE
        imageViewMic!!.rotation = 0f
        isDeleting = true
        imageViewAudio!!.isEnabled = false
        handler!!.postDelayed({
            isDeleting = false
            imageViewAudio!!.isEnabled = true
            if (showAttachmentIcon) {
                imageViewAttachment!!.visibility = View.VISIBLE
            }
            if (showCameraIcon) {
                btnGallery!!.visibility = View.VISIBLE
            }
        }, 1250)
        imageViewMic!!.animate().translationY(-dp * 150).rotation(180f).scaleXBy(0.6f)
            .scaleYBy(0.6f).setDuration(500).setInterpolator(
                DecelerateInterpolator()
            ).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    var displacement = 0f
                    displacement = if (isLayoutDirectionRightToLeft) {
                        dp * 40
                    } else {
                        -dp * 40
                    }
                    dustin!!.translationX = displacement
                    dustin_cover!!.translationX = displacement
                    dustin_cover!!.animate().translationX(0f).rotation(-120f).setDuration(350)
                        .setInterpolator(
                            DecelerateInterpolator()
                        ).start()
                    dustin!!.animate().translationX(0f).setDuration(350).setInterpolator(
                        DecelerateInterpolator()
                    ).setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            dustin!!.visibility = View.VISIBLE
                            dustin_cover!!.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {}
                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    }).start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    imageViewMic!!.animate().translationY(0f).scaleX(1f).scaleY(1f).setDuration(350)
                        .setInterpolator(
                            LinearInterpolator()
                        ).setListener(
                            object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}
                                override fun onAnimationEnd(animation: Animator) {
                                    imageViewMic!!.visibility = View.INVISIBLE
                                    imageViewMic!!.rotation = 0f
                                    var displacement = 0f
                                    displacement = if (isLayoutDirectionRightToLeft) {
                                        dp * 40
                                    } else {
                                        -dp * 40
                                    }
                                    dustin_cover!!.animate().rotation(0f).setDuration(150).setStartDelay(50)
                                        .start()
                                    dustin!!.animate().translationX(displacement).setDuration(200)
                                        .setStartDelay(250).setInterpolator(
                                            DecelerateInterpolator()
                                        ).start()
                                    dustin_cover!!.animate().translationX(displacement).setDuration(200)
                                        .setStartDelay(250).setInterpolator(
                                            DecelerateInterpolator()
                                        ).setListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(animation: Animator) {}
                                            override fun onAnimationEnd(animation: Animator) {
                                                editTextMessage!!.visibility = View.VISIBLE
                                                editTextMessage!!.requestFocus()
                                            }

                                            override fun onAnimationCancel(animation: Animator) {}
                                            override fun onAnimationRepeat(animation: Animator) {}
                                        }).start()
                                }

                                override fun onAnimationCancel(animation: Animator) {}
                                override fun onAnimationRepeat(animation: Animator) {}
                            }
                        ).start()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).start()
    }

    private fun showErrorLog(s: String) {
        Log.e("TAG", s)
    }

}