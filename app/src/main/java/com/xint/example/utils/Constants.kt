package com.xint.example.utils

object Constants {

    const val socketUrl = "http://150.230.54.239:8100"
    const val serverUrl = "http://150.230.54.239:3000"
    const val sessionId = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7Im1vYmlsZU5vIjoiOTY2NTQ0NTU1NTI3In0sImlhdCI6MTY3NjQ2NzkxMywiZXhwIjoxNjc2NTM5OTEzfQ.eeyHuVrpAbq-mKodPexavsuNmbYM59wdrfVQIEXeiyY"
    const val userId = "966222789374"
    const val sessionId2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7Im1vYmlsZU5vIjoiOTIzMDE0NTY4MjYzIn0sImlhdCI6MTY3NjQ2ODE0NSwiZXhwIjoxNjc2NTQwMTQ1fQ.4HRJWSC5JhNQgZHZ8ZN9cSN_nGJnCrK9RMEFRpgmpfw"
    const val userId2 = "966110000021"

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

}