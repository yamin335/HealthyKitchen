package com.rtchubs.restohubs.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Chapter(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("image")
    @Expose
    val image: Int? = null,


    @SerializedName("chapter_url")
    @Expose
    val chapterVideoUrl: String? = null

)