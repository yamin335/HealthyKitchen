package com.rtchubs.restohubs.api

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["userId"])
/*@RoomDataConverter(RoomDataConverter::class)*/
data class ProfileInfo(
    @SerializedName("userId")
    @Expose
    var userId: Int,
    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String?,
    @SerializedName("deviceId")
    @Expose
    var deviceId: String?,
    @SerializedName("deviceName")
    @Expose
    var deviceName: String?,
    @SerializedName("deviceModel")
    @Expose
    var deviceModel: String?,
    @SerializedName("profileImage")
    @Expose
    var profileImage: String?,
    @SerializedName("occupation")
    @Expose
    var occupation: String? = null,
    @SerializedName("organization")
    @Expose
    var organization: String? = null,
    @SerializedName("dateOfBirth")
    @Expose
    var dateOfBirth: String?
) {

}