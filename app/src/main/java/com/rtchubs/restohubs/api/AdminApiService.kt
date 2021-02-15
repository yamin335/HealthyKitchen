package com.rtchubs.restohubs.api

import com.google.gson.JsonObject
import com.rtchubs.restohubs.api.Api.ContentType
import com.rtchubs.restohubs.models.AllMerchantResponse
import com.rtchubs.restohubs.models.AllProductResponse
import com.rtchubs.restohubs.models.AllShoppingMallResponse
import com.rtchubs.restohubs.models.OrderResponse
import com.rtchubs.restohubs.models.common.MyAccountListResponse
import com.rtchubs.restohubs.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.restohubs.models.payment_account_models.BankOrCardListResponse
import com.rtchubs.restohubs.models.registration.InquiryResponse
import com.rtchubs.restohubs.models.registration.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface AdminApiService {
    @Headers(ContentType)
    @POST(ApiEndPoint.SALES)
    suspend fun placeOrder(
        @Body jsonObject: JsonObject
    ): Response<OrderResponse>
}
