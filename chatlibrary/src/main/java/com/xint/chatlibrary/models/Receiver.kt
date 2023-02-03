package com.xint.chatlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Receiver(
    var englishName: String = "",
    var arabicName: String ="",
    var profileImage: String ="",
    var userId: Int = 0
) : Parcelable