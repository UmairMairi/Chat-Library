package com.xint.chatfooter

class AttachmentOption(
    private var id: Int,
    private var title: String?,
    private var resourceImage: Int
) {

    companion object {
        const val DOCUMENT_ID: Int = 101
        const val CAMERA_ID: Int = 102
        const val GALLERY_ID: Int = 103
        const val AUDIO_ID: Int = 104
        const val LOCATION_ID: Int = 105
        const val CONTACT_ID: Int = 106
        fun getDefaultList(): List<AttachmentOption> {
            val attachmentOptions: MutableList<AttachmentOption> =
                ArrayList()
            attachmentOptions.add(
                AttachmentOption(
                    DOCUMENT_ID,
                    "Document",
                    R.drawable.ic_attachment_document
                )
            )
            attachmentOptions.add(
                AttachmentOption(
                    CAMERA_ID,
                    "Camera",
                    R.drawable.ic_attachment_camera
                )
            )
            attachmentOptions.add(
                AttachmentOption(
                    GALLERY_ID,
                    "Gallery",
                    R.drawable.ic_attachment_gallery
                )
            )
            attachmentOptions.add(
                AttachmentOption(
                    AUDIO_ID,
                    "Audio",
                    R.drawable.ic_attachment_audio
                )
            )
            attachmentOptions.add(
                AttachmentOption(
                    LOCATION_ID,
                    "Location",
                    R.drawable.ic_attachment_location
                )
            )
            attachmentOptions.add(
                AttachmentOption(
                    CONTACT_ID,
                    "Contact",
                    R.drawable.ic_attachment_contact
                )
            )
            return attachmentOptions
        }
    }


    fun getId(): Int {
        return id
    }

    fun getTitle(): String? {
        return title
    }

    fun getResourceImage(): Int {
        return resourceImage
    }
}