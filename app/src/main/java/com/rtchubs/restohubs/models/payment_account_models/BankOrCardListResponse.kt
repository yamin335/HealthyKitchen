package com.rtchubs.restohubs.models.payment_account_models

import java.io.Serializable

data class BankOrCardListResponse(val body: BankOrCardListBody?, val errorMessage: Any?, val timeGenerated: String?, val isSuccess: Boolean?)

data class BankOrCardListBody(val banks: List<BankOrCard>?)

data class BankOrCard(val id: Int?, val name: String?, val imageUrl: String?, val isCardLinkAllowed: Boolean?, val isBankLinkAllowed: Boolean?): Serializable