package com.rtchubs.restohubs.models.payment_account_models

data class AddCardOrBankResponse(val body: AddCardOrBankBody?, val errorMessage: String?, val timeGenerated: String?, val isSuccess: Boolean?)

data class AddCardOrBankBody(val isSuccess: Boolean?, val message: String?, val accountNumber: String?)