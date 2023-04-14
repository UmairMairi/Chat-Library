package com.xint.example.utils

object Constants {

    const val socketUrl = "http://144.24.208.108:8100"
    const val serverUrl = "http://144.24.208.108:3000"

    const val receiverId = "123"
    const val senderId = "321"

    object ChatViewType {
        const val TEXT_MESSAGE = 0
        const val TEXT_MESSAGE_SELF = 1
        const val TRANSACTION = 2
        const val TRANSACTION_SELF = 3
        const val PICTURE_MESSAGE = 4
        const val PICTURE_MESSAGE_SELF = 5
        const val LOCATION_MESSAGE = 6
        const val LOCATION_MESSAGE_SELF = 7
        const val AUDIO_MESSAGE = 8
        const val AUDIO_MESSAGE_SELF = 9
        const val VIDEO_MESSAGE = 10
        const val VIDEO_MESSAGE_SELF = 11

        const val TOP_MESSAGE = 0
        const val MIDDLE_MESSAGE = 1
        const val BOTTOM_MESSAGE = 2

        const val GALLERY_MEDIA_REQUEST_CODE = 11
        const val PAYMENT_REQUEST_CODE = 22
        const val CAPTURE_IMAGE_VIDEO_REQUEST_CODE = 23
        const val IMAGE_CROP_REQUEST_CODE = 24
        const val IMAGE_PREVIEW_PATH = "image_preview_path"
        const val VIDEO_PREVIEW_PATH = "video_preview_path"
        const val FILE_TYPE = "file_path"
        const val BODY = "body"
    }

    object KeysExtras{
        const val conversation = "conversation"
    }

    object Activity{
        const val stack = "activity-stack"
    }
    object OnBoarding{
        const val transactionID = "login-tid"
        const val loginModel = "login-model"
        const val RIDER_LOGIN_REASON = "3"
        const val RIDER_SIGNUP_REASON = "4"
        const val DRIVER_LOGIN_REASON = "1"
        const val DRIVER_SIGNUP_REASON = "2"
    }

}