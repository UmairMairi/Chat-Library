package com.xint.chatfooter

import android.annotation.SuppressLint

class AttachmentOption {
    private var id = 0
    private var title: String? = null
    private var resourceImage = 0

    val DOCUMENT_ID: Int = 101
    val CAMERA_ID: Int = 102
    val GALLERY_ID: Int = 103
    val AUDIO_ID: Int = 104
    val LOCATION_ID: Int = 105
    val CONTACT_ID: Int = 106

    fun getDefaultList(): List<AttachmentOption> {
        val attachmentOptions: MutableList<AttachmentOption> =
            ArrayList<AttachmentOption>()
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

    @SuppressLint("NotConstructor")
    fun AttachmentOption(id: Int, title: String?, resourceImage: Int): AttachmentOption {
        this.id = id
        this.title = title
        this.resourceImage = resourceImage
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