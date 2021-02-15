package com.rtchubs.restohubs.ui.order

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.restohubs.api.*
import com.rtchubs.restohubs.local_db.dao.CartDao
import com.rtchubs.restohubs.local_db.dbo.CartItem
import com.rtchubs.restohubs.models.Order
import com.rtchubs.restohubs.models.OrderResponse
import com.rtchubs.restohubs.models.payment_account_models.AddCardOrBankResponse
import com.rtchubs.restohubs.repos.OrderRepository
import com.rtchubs.restohubs.ui.common.BaseViewModel
import com.rtchubs.restohubs.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderViewModel @Inject constructor(private val application: Application, private val cartDao: CartDao, private val orderRepository: OrderRepository) : BaseViewModel(application) {

    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val mobile: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val address: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val orderResponse: MutableLiveData<OrderResponse> by lazy {
        MutableLiveData<OrderResponse>()
    }

    fun placeOrder(order: Order) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                orderResponse.postValue(null)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(orderRepository.placeOrderRepo(order))) {
                    is ApiSuccessResponse -> {
                        orderResponse.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        orderResponse.postValue(null)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        orderResponse.postValue(null)
                    }
                }
            }
        }
    }
}