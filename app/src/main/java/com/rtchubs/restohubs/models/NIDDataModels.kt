package com.rtchubs.restohubs.models

import android.net.Uri
import java.io.Serializable

data class NIDFrontModel(var isValidNID: Boolean = false, var name: String = "", var birthDate: String = "", var nidNo: String = "", var image: Uri? = null): Serializable
data class NIDBackModel(var isValidNID: Boolean = false, var bloodGroup: String = "", var birthPlace: String = "", var nidIssueDate: String = "", var image: Uri? = null): Serializable
data class NIDDataModels(val isValidNID: Boolean = false, val frontData: NIDFrontModel, val backData: NIDBackModel): Serializable