package com.rtchubs.restohubs.repos

import com.rtchubs.restohubs.api.ApiService
import com.rtchubs.restohubs.models.registration.InquiryResponse
import com.rtchubs.restohubs.models.registration.DefaultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun inquireRepo(mobileNumber: String, deviceId: String): Response<InquiryResponse> {
        return withContext(Dispatchers.IO) {
            apiService.inquire(mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceId.toRequestBody("text/plain".toMediaTypeOrNull()))
        }
    }

    suspend fun requestOTPRepo(mobileNumber: String, hasGivenConsent: String): Response<DefaultResponse> {
        return withContext(Dispatchers.IO) {
            apiService.requestOTP(
                mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                hasGivenConsent.toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }
    }

    suspend fun registrationRepo(mobileNumber: String, otp: String,
                                 password: String, fullName: String,
                                 mobileOperator: String, deviceId: String,
                                 deviceName: String, deviceModel: String,
                                 nidNumber: String,
                                 nidFrontImage: MultipartBody.Part?,
                                 nidBackImage: MultipartBody.Part?,
                                 userImage: MultipartBody.Part?): Response<DefaultResponse> {

        return withContext(Dispatchers.IO) {
            apiService.registration(
                mobileNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                otp.toRequestBody("text/plain".toMediaTypeOrNull()),
                password.toRequestBody("text/plain".toMediaTypeOrNull()),
                fullName.toRequestBody("text/plain".toMediaTypeOrNull()),
                mobileOperator.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceId.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceName.toRequestBody("text/plain".toMediaTypeOrNull()),
                deviceModel.toRequestBody("text/plain".toMediaTypeOrNull()),
                userImage,
                nidNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                nidFrontImage, nidBackImage
            )
        }
    }
}