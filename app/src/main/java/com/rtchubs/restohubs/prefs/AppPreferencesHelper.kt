package com.rtchubs.restohubs.prefs


import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rtchubs.restohubs.api.ProfileInfo
import com.rtchubs.restohubs.api.TokenInformation
import com.rtchubs.restohubs.di.PreferenceInfo
import com.rtchubs.restohubs.worker.TokenRefreshWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class AppPreferencesHelper @Inject constructor(
    context: Context,
    @PreferenceInfo prefFileName: String
) : PreferencesHelper {

    private val prefs = lazy {
        // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }

    override var test by StringPreference(prefs, "test", defaultValue = null, commit = true)
    override var isRegistered by BooleanPreference(prefs, KEY_REG, defaultValue = false, commit = true)
    override var isTermsAccepted by BooleanPreference(prefs, KEY_TERMS, defaultValue = false, commit = true)
    override var pinNumber by StringPreference(prefs, KEY_PIN, defaultValue = null, commit = true)
    override var mobileNo by StringPreference(prefs, KEY_MOBILE, defaultValue = null, commit = true)
    override var operator by StringPreference(prefs, KEY_OPERATOR, defaultValue = null, commit = true)
    override var deviceId by StringPreference(prefs, KEY_DEVICE_ID, defaultValue = null, commit = true)
    override var deviceName by StringPreference(prefs, KEY_DEVICE_NAME, defaultValue = null, commit = true)
    override var deviceModel by StringPreference(prefs, KEY_DEVICE_MODEL, defaultValue = null, commit = true)
    override var isLoggedIn by BooleanPreference(prefs, KEY_IS_LOGGED_IN, defaultValue = false, commit = true)

    override var accessToken by StringPreference(prefs, PREF_KEY_ACCESS_TOKEN, null, true)

    override var refreshToken by StringPreference(prefs, PREF_KEY_REFRESH_TOKEN, null, true)

    override var userRole by StringPreference(prefs, KEY_USER_ROLE, null, true)

    override var userId by IntPreference(prefs, KEY_USER_ID, -1, true)

    override var phoneNumber by StringPreference(prefs, KEY_PHONE_NUMBER, null, true)

    override var accessTokenExpiresIn by LongPreference(prefs, PREF_KEY_ACCESS_TOKEN_EXPIRES_IN, 0)

    override fun logoutUser() {
        prefs.value.edit {
            remove(KEY_IS_LOGGED_IN)
            remove(PREF_KEY_ACCESS_TOKEN)
            remove(PREF_KEY_ACCESS_TOKEN_EXPIRES_IN)
            remove(PREF_KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_PHONE_NUMBER)
            remove(KEY_SAMMITEE_CODE)
            remove(KEY_BRANCH_CODE)
            remove(KEY_USER_ROLE)
        }
    }

    private fun getCurTime(): Long {
        return System.currentTimeMillis()
    }

    override val isAccessTokenExpired
        get() = getCurTime() > accessTokenExpiresIn


    /*save auth token and login status*/
    override fun saveToken(tokenInformation: TokenInformation) {
        accessToken = tokenInformation.accessToken.toString()
        accessTokenExpiresIn = getCurTime() + (tokenInformation.expire?.times(1000) ?: 10000)
        refreshToken = tokenInformation.refreshToken.toString()
        userRole = tokenInformation.userRole.toString()
        isLoggedIn = true
        scheduleRefreshToken(tokenInformation.expire)
    }

    /*save user Profile*/
    override fun saveUserProfile(profile: ProfileInfo) {
        userId = profile.userId
        phoneNumber = profile.phoneNumber
    }

    /*schedule refresh token job*/
    private fun scheduleRefreshToken(expiresIn: Long?) {
        // Create a Constraints object that defines when the task should run
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

// ...then create a OneTimeWorkRequest that uses those constraints
        val tokenRefreshWork = OneTimeWorkRequestBuilder<TokenRefreshWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInitialDelay(expiresIn ?: 10000, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance().enqueueUniqueWork(
            "refreshTokenWork",
            ExistingWorkPolicy.REPLACE, tokenRefreshWork
        )
    }

    override fun getToken(): TokenInformation = TokenInformation(
        accessToken = accessToken,
        expire = accessTokenExpiresIn,
        refreshToken = refreshToken,
        userRole = userRole
    )

    override fun getAccessTokenHeader(): String {
        return getAuthHeader(accessToken)
    }

    override fun getAuthHeader(token: String?): String {
        return /*$tokenType */"Bearer $token"
    }

    override var validityLimiterMap: MutableMap<String, Long>?
        get() {
            val jsonUser = prefs.value.getString(KEY_VALIDITY_RATE_MAP, null)
            return jsonUser?.let {
                Gson().fromJson(jsonUser, object : TypeToken<MutableMap<String, Long>?>() {}.type)
            }
        }
        set(value) {
            val jsonMap =
                Gson().toJson(value, object : TypeToken<MutableMap<String, Long>?>() {}.type)
            prefs.value.edit { putString(KEY_VALIDITY_RATE_MAP, jsonMap) }
        }

    override var language by StringPreference(prefs, KEY_LANGUAGE, LANGUAGE_BENGALI)


    companion object {
        // QPay
        private const val KEY_REG = "RegistrationStatus"
        private const val KEY_TERMS = "TermsAndConditionStatus"
        private const val KEY_PIN = "PinNumber"
        private const val KEY_MOBILE = "MobileNo"
        private const val KEY_OPERATOR = "MobileOperator"
        private const val KEY_DEVICE_ID = "DeviceId"
        private const val KEY_DEVICE_NAME = "DeviceName"
        private const val KEY_DEVICE_MODEL = "DeviceModel"
        private const val KEY_IS_LOGGED_IN = "LoginStatus"

        private const val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"

        private const val PREF_KEY_ACCESS_TOKEN_EXPIRES_IN = "PREF_KEY_ACCESS_TOKEN_EXPIRES_IN"

        private const val PREF_KEY_REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN"

        private const val KEY_USER_ROLE = "USER_ROLE"
        private const val KEY_PHONE_NUMBER = "PHONE_NUMBER"
        private const val KEY_SAMMITEE_CODE = "SAMMITEE_CODE"
        private const val KEY_BRANCH_CODE = "BRANCH_CODE"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_VALIDITY_RATE_MAP = "KEY_VALIDITY_RATE"
        private const val KEY_LANGUAGE = "KEY_LANGUAGE"
        const val LANGUAGE_BENGALI = "bn"
        const val LANGUAGE_ENGLISH = "en"

    }

}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit(commit) { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit(commit) { putString(name, value) }
    }
}

class IntPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Int,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.value.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.value.edit(commit) { putInt(name, value) }
    }
}

class LongPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit(commit) { putLong(name, value) }
    }
}
