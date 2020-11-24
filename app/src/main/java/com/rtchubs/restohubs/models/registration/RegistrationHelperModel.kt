package com.rtchubs.restohubs.models.registration

import java.io.Serializable

data class RegistrationHelperModel(var isRegistered: Boolean = false, var mobile: String = "", var operator: String = "", var isTermsAccepted: Boolean = false, var otp: String = "", var pinNumber: String = ""): Serializable