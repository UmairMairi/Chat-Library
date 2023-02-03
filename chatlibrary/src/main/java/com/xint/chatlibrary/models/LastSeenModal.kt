package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastSeenModal(
    val onlineStatus: Boolean
):Parcelable