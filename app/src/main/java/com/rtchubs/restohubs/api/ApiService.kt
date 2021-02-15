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
interface ApiService {

    @Multipart
    @POST(ApiEndPoint.INQUIRE)
    suspend fun inquire(@Part("PhoneNumber") mobileNumber: RequestBody, @Part("DeviceId") deviceId: RequestBody): Response<InquiryResponse>

    @Multipart
    @POST(ApiEndPoint.REQUESTOTP)
    suspend fun requestOTP(
        @Part("PhoneNumber") mobileNumber: RequestBody,
        @Part("HasGivenConsent") hasGivenConsent: RequestBody
    ): Response<DefaultResponse>

    @Multipart
    @POST(ApiEndPoint.REGISTRATION)
    suspend fun registration(
        @Part("MobileNumber") mobileNumber: RequestBody,
        @Part("Otp") otp: RequestBody,
        @Part("Pin") password: RequestBody,
        @Part("FullName") fullName: RequestBody,
        @Part("MobileOperator") mobileOperator: RequestBody,
        @Part("DeviceId") deviceId: RequestBody,
        @Part("DeviceName") deviceName: RequestBody,
        @Part("DeviceModel") deviceModel: RequestBody,
        @Part userImage: MultipartBody.Part?,
        @Part("NidNumber") nidNumber: RequestBody,
        @Part nidFrontImage: MultipartBody.Part?,
        @Part nidBackImage: MultipartBody.Part?
    ): Response<DefaultResponse>


    @FormUrlEncoded
    @POST(ApiEndPoint.CONNECT_TOKEN)
    suspend fun connectToken(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String,
        @Field("scope") scope: String,
        @Field("device_id") deviceID: String,
        @Field("device_name") deviceName: String,
        @Field("device_model") deviceModel: String,
        @Field("client_id") clientID: String,
        @Field("client_secret") clientSecret: String,
        @Field("otp") otp: String
    ): Response<String>


    @GET(ApiEndPoint.GET_BANK_LIST)
    suspend fun requestBankList(
        @Query("type") type: String,
        @Header("Authorization") token: String
    ): Response<BankOrCardListResponse>

    @Headers(ContentType)
    @POST(ApiEndPoint.ADD_CARD)
    suspend fun addBankAccount(
        @Body jsonObject: JsonObject,
        @Header("Authorization") token: String
    ): Response<AddCardOrBankResponse>

    @Headers(ContentType)
    @POST(ApiEndPoint.ADD_BANK)
    suspend fun addCardAccount(
        @Body jsonObject: JsonObject,
        @Header("Authorization") token: String
    ): Response<AddCardOrBankResponse>

    @GET(ApiEndPoint.MY_ACCOUNT_LIST)
    suspend fun myAccountList(
        @Header("Authorization") token: String
    ): Response<MyAccountListResponse>


    // eDokanPat
    @GET(ApiEndPoint.ALL_MALL)
    suspend fun getAllMalls(): Response<AllShoppingMallResponse>

    @GET(ApiEndPoint.ALL_MERCHANTS)
    suspend fun getAllMerchants(): Response<AllMerchantResponse>

    @GET(ApiEndPoint.MERCHANT_PRODUCTS)
    suspend fun getAllProducts(
        @Path("id") type: String
    ): Response<AllProductResponse>
}
