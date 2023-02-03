package com.xint.chatlibrary.utils

object ChatConstants {
    /** Socket Request Keys**/

    object SubscribeUser {
        const val userId = "userID"
    }


    object Chat {
        object ChatType {
            const val SINGLE_CHAT = 1
            const val GROUP_CHAT = 2
        }

        object ChatListType {
            const val NORMAL_LIST = 1
            const val ARCHIVED_LIST = 2
            const val BLOCKED_LIST = 3
        }

        object ChatMessageType {
            const val TEXT = 1
            const val IMAGE = 2
            const val AUDIO = 3
            const val VIDEO = 4
            const val DOCUMENT = 5
            const val CONTACT = 6
            const val LOCATION = 7
            const val ACTION = 8
            const val PAYMENT = 9
        }

        object ChatMessageStatus {
            const val WAITING = "0"
            const val SEND = "1"
            const val UNKNOWN = "2"
            const val DELIVERED = "3"
            const val READ = "4"
        }

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

        object ChatFileType {
            const val IMAGE = "image"
            const val VIDEO = "video"
        }

        object ChatMimeType {
            const val IMAGE = "image/jpeg"
            const val VIDEO = "video/mp4"
            const val AUDIO = "audio/mpeg"
        }
    }


    object Payment {
        const val TRANSACTION_FAILED = 0
        const val TRANSACTION_SUCCESS = 1
        var isPaymentFailed = false
    }
}