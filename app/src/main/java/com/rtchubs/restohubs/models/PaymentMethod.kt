package com.rtchubs.restohubs.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("image")
    @Expose
    val image: Any? = null
)