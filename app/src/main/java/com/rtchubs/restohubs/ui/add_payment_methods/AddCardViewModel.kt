package com.rtchubs.restohubs.ui.add_payment_methods

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.restohubs.api.*
import com.rtchubs.restohubs.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.repos.HomeRepository
import com.rtchubs.restohubs.ui.common.BaseViewModel
import com.rtchubs.restohubs.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCardViewModel @Inject constructor(
    private val application: Application,
    private val repository: HomeRepository,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel(application) {

    var bankId = 0

    val cardNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val expiryMonth: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val expiryYear: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val addCardAccountResponse: MutableLiveData<AddCardOrBankResponse> by lazy {
        MutableLiveData<AddCardOrBankResponse>()
    }

    fun addCardAccount() {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {

                when (val apiResponse = ApiResponse.create(repository.addCardRepo(bankId, cardNumber.value!!,
                    expiryMonth.value?.toInt()!!, expiryYear.value?.toInt()!!, preferencesHelper.getAccessTokenHeader()))) {
                    is ApiSuccessResponse -> {
                        addCardAccountResponse.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        addCardAccountResponse.postValue(Gson().fromJson(apiResponse.errorMessage, AddCardOrBankResponse::class.java))
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }
}