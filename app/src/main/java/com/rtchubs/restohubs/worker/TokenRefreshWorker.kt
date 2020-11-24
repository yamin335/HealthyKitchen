package com.rtchubs.restohubs.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rtchubs.restohubs.api.TokenAuthenticator
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.util.CommonUtils
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Priyanka on 2019-08-21.
 */
class TokenRefreshWorker(private val tokenAuthenticator: TokenAuthenticator,
                         private val preferencesHelper: PreferencesHelper,
                         appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Get the input
        val accessToken = preferencesHelper.accessToken ?: return Result.failure()
        // Do the work here--in this case, upload the images.
        return try {
            if (preferencesHelper.isLoggedIn && preferencesHelper.isAccessTokenExpired)
                if (tokenAuthenticator.refreshToken(accessToken) == null) {
                    /* token cannot be refreshed. logout user */
                    preferencesHelper.logoutUser()
                    CommonUtils.sessionOut()
                    Result.failure()
                }
            Result.success()
        } catch (e: IOException) {
            e.printStackTrace()
            Result.retry()
        }

        // Indicate whether the task finished successfully with the Result
    }

    class Factory @Inject constructor(
            private val tokenAuthenticator: TokenAuthenticator,
            private val preferencesHelper: PreferencesHelper
    ) : DaggerWorkerFactory.ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker =
                TokenRefreshWorker(tokenAuthenticator, preferencesHelper, appContext, params)
    }
}