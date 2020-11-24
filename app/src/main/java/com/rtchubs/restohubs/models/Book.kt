package com.rtchubs.restohubs.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Book(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    val listOfSubDoctors: List<SubBook>
    /*get() =  as List<SubDoctor>*/


)