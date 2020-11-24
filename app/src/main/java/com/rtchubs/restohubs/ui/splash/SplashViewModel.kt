package com.rtchubs.restohubs.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rtchubs.restohubs.prefs.PreferencesHelper
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val preferencesHelper: PreferencesHelper) :
    ViewModel() {

    private val authenticationState = MutableLiveData<AuthenticationState>()
    private val isOnBoarded = false

    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,        // The user has authenticated successfully
    }

    init {
        checkIfUserAuthenticated()
    }

    private fun checkIfUserAuthenticated() {
        authenticationState.value = if (/*preferencesHelper.isLoggedIn*/ isOnBoarded)
            AuthenticationState.AUTHENTICATED
        else
            AuthenticationState.UNAUTHENTICATED
    }

    fun getAuthenticationState() = authenticationState
}