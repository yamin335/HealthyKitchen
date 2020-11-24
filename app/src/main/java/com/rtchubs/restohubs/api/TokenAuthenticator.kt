package com.rtchubs.restohubs.api


import com.rtchubs.restohubs.prefs.PreferencesHelper
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Singleton

@Singleton
class TokenAuthenticator(
    private val preferencesHelper: PreferencesHelper,
    private val apiService: ApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // We need to have a token in order to refresh it.
        val token = preferencesHelper.accessToken ?: return null

        synchronized(this) {
            // Check if the request made was previously made as an authenticated request.
            if (response.request.header("AUTH_HEADER_NAME") != null) {
                val newToken = try {
                    refreshToken(token)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                } ?: return null

                // Retry the request with the new token.
                return response.request
                    .newBuilder()
                    .header("AUTH_HEADER_NAME", preferencesHelper.getAuthHeader(newToken))
                    .build()
            }
        }
        return null
    }

    @Synchronized
    fun refreshToken(oldToken: String): String? {
        val newToken = preferencesHelper.accessToken
        /*if token changed return the new token*/
        if (oldToken != newToken) return newToken

        val tokenRequestModel = RefreshRequestModel()
        tokenRequestModel.token = preferencesHelper.refreshToken

//        val tokenInfo = apiService.refresh(tokenRequestModel.convert()).execute().body() ?: return null
//        preferencesHelper.saveToken(tokenInfo)
//
//        return tokenInfo.accessToken
        return null
    }

}
