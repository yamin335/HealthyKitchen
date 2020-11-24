package com.rtchubs.restohubs.models.common

data class MyAccount(val accountId: Number?, val accountNumber: String?, val bank: String?, val type: String?)

data class MyAccountListResponse(val body: MyAccountList?, val errorMessage: String?, val timeGenerated: String?, val isSuccess: Boolean?)

data class MyAccountList(val accounts: List<MyAccount>?)