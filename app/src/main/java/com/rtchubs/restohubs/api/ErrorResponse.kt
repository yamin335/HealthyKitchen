package com.rtchubs.restohubs.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ErrorResponse : BaseResponse() {
    @SerializedName("details", alternate = ["Details", "error_description"])
    @Expose
    var details: String? = null

    @SerializedName("traceId")
    @Expose
    var traceId: String? = null

    @SerializedName("errors")
    @Expose
    var errors: Map<String, Array<String>>? = null

    /*if validation error contains one field only show error message from errors array */
    override fun toString(): String {
        return errors?.values?.firstOrNull()?.firstOrNull() ?: getDetailsOrMsg()
    }

    fun getDetailsOrMsg(): String {
        return if (details.isNullOrBlank())
            "$message"
        else "$details"/* +
                if (*//*BuildConfig.DEBUG && *//*statusCode!= null) " ($statusCode)"*//*show status code in debug build only*//*
                else ""*/
    }
}