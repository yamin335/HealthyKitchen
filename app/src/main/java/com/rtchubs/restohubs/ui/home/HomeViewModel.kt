package com.rtchubs.restohubs.ui.home

import android.app.Application
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rtchubs.restohubs.R
import com.rtchubs.restohubs.api.*
import com.rtchubs.restohubs.local_db.dao.CartDao
import com.rtchubs.restohubs.local_db.dao.FavoriteDao
import com.rtchubs.restohubs.local_db.dbo.CartItem
import com.rtchubs.restohubs.models.*
import com.rtchubs.restohubs.models.registration.DefaultResponse
import com.rtchubs.restohubs.prefs.PreferencesHelper
import com.rtchubs.restohubs.repos.HomeRepository
import com.rtchubs.restohubs.ui.common.BaseViewModel
import com.rtchubs.restohubs.util.AppConstants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val preferencesHelper: PreferencesHelper,
    private val application: Application,
    private val repository: HomeRepository,
    private val cartDao: CartDao,
    private val favoriteDao: FavoriteDao
) : BaseViewModel(application) {

    companion object {
        var allCategoryWiseProducts: ArrayList<CategoryWithProducts>? = null
        var isDataLoaded = false
    }

    var totalCategory = 0
    var countCategory = 0
    val loadDataToAdapter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val cartItemCount: LiveData<Int> = liveData {
        cartDao.getCartItemsCount().collect { count ->
            emit(count)
        }
    }

    val filteredProductCategories: MutableLiveData<List<RProductCategory>> by lazy {
        MutableLiveData<List<RProductCategory>>()
    }

    val filteredProduct: MutableLiveData<CategoryWithProducts> by lazy {
        MutableLiveData<CategoryWithProducts>()
    }

    val showSnackbar: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun addToFavorite(product: Product) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                val response = favoriteDao.addItemToFavorite(product)
                if (response == -1L) {
                    toastWarning.postValue("Already added to favorite!")
                } else {
                    toastSuccess.postValue("Added to favorite")
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        try {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            viewModelScope.launch(handler) {
                val response = cartDao.addItemToCart(CartItem(product.id, product, quantity))
                if (response == -1L) {
                    incrementCartItemQuantity(product.id)
                }
                toastSuccess.postValue("Added to cart")
                showSnackbar.postValue(true)
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    fun incrementCartItemQuantity(id: Int) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        viewModelScope.launch(handler) {
            cartDao.increaseProductQuantity(id)
        }
    }

    fun getFilteredProductCategories(filter: Map<String, String>) {
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.requestFilteredProductCategory(filter))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        filteredProductCategories.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        }
    }

    fun getFilteredProduct(category: RProductCategory) {
        val filter: Map<String, String> = mapOf("category" to category.id.toString())
        if (checkNetworkStatus()) {
            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                toastError.postValue(AppConstants.serverConnectionErrorMessage)
                loadDataToAdapter.postValue(++countCategory)
            }

            apiCallStatus.postValue(ApiCallStatus.LOADING)
            viewModelScope.launch(handler) {
                when (val apiResponse = ApiResponse.create(repository.requestFilteredProduct(filter))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        filteredProduct.postValue(CategoryWithProducts(category, apiResponse.body))
                        loadDataToAdapter.postValue(++countCategory)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        loadDataToAdapter.postValue(++countCategory)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                        loadDataToAdapter.postValue(++countCategory)
                    }
                }
            }
        }
    }
}