package com.xint.chatlibrary.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.xint.chatlibrary.utils.ChatConstants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatMessageModel(
    var user_id:String,
    var messageId:String,
    var message_date: String,
    var messageStatus: String,
    var senderProfileImage: String,
    var viewType: Int  = ChatConstants.Chat.ChatViewType.TEXT_MESSAGE,
    var message_model:MessageModel? = null
): Parcelable

@Parcelize
data class MessageModel(
    val location: LocationMessageModel? = null,
    var messageString: String? = null,
    var imageUri:String? = null,
    var videoUri:String? = null,
    var videoPreviewUri:String? = null,
    var payment:PaymentModel? = null,
    var audioFileUri:String? = null,
    var mediaIdentifier:String? = null

): Parcelable

@Parcelize
data class LocationMessageModel(
    var latLng: LatLng? = null,
    var locationName:String? = null,
    var locationDescription:String?= null

): Parcelable

@Parcelize
data class PaymentModel(
    var transactionId:String? = null,
    var status:Int? = ChatConstants.Payment.TRANSACTION_FAILED,
    var amount:String?=null,
    var message:String?= null
): Parcelable
