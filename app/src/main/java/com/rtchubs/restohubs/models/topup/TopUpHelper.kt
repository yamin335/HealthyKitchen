package com.rtchubs.restohubs.models.topup

import java.io.Serializable

data class TopUpHelper(var mobile: String = "", var operator: String = "", var simType: String = "", var amount: String = "0.0", var pin: String = ""): Serializable