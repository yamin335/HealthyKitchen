package com.rtchubs.restohubs.repos

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rtchubs.restohubs.api.AdminApiService
import com.rtchubs.restohubs.api.ApiService
import com.rtchubs.restohubs.api.RestoHubsApiService
import com.rtchubs.restohubs.models.*
import com.rtchubs.restohubs.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.restohubs.models.payment_account_models.BankOrCardListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(private val adminApiService: AdminApiService) {
    suspend fun placeOrderRepo(order: Order): Response<OrderResponse> {
        return withContext(Dispatchers.IO) {
            adminApiService.placeOrder(Gson().toJsonTree(order).asJsonObject)
        }
    }
}