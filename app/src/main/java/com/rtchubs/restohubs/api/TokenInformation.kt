package com.rtchubs.restohubs.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TokenInformation(
    @SerializedName("token")
    @Expose
    var accessToken: String?,
    @SerializedName("expire")
    @Expose
    var expire: Long?,
    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String?,
    @SerializedName("userRole")
    @Expose
    var userRole: String?
) : Serializable