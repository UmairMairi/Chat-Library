package com.xint.example.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class UserDetailsModel(
    val statusCode: Long? = null,
    val data: User? = null
) : Parcelable {
    @Parcelize
    data class User(
        val id: @RawValue Any? = null,
        val userId: String? = null,
        val idNumber: String? = null,
        val userStatus: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val arabicFirstName: String? = null,
        val arabicLastName: String? = null,
        val emailId: String? = null,
        val mobileNo: String? = null,
        val dateOfBirth: String? = null,
        val gender: String? = null,
        val address1: String? = null,
        val address2: String? = null,
        val activationDate: @RawValue Any? = null,
        val creationDate: @RawValue Any? = null,
        val modificationDate: @RawValue Any? = null,
        val deviceToken: String? = null,
        val clientOS: String? = null,
        val prefferedLanguage: String? = null,
        val profileImage: String? = null,
        val latitude: @RawValue Any? = null,
        val longitude: @RawValue Any? = null,
        val additionalInfo: @RawValue Any? = null,
        val totalRides: Long? = null,
        val upcomingRides: Long? = null,
        val ridesCancelled: Long? = null,
        val totalTrips: Long? = null,
        val tripsDeclined: Long? = null,
        val tripsCancelled: Long? = null,
        val totalEarned: Double? = null,
        val totalSpent: Double? = null,
        val userType: Long? = null,
        val driverID: String? = null,
        val isRider: Boolean? = null,
        val fullName: String? = null,
        val arabicFullName: String? = null
    ) : Parcelable {
        @Parcelize
        data class ID(
            val id: String? = null,
            val status: String? = null,
            val authStatus: String? = null
        ) : Parcelable
    }

}



