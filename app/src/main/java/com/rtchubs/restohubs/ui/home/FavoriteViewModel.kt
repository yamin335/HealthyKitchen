package com.rtchubs.restohubs.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rtchubs.restohubs.local_db.dao.CartDao
import com.rtchubs.restohubs.local_db.dao.FavoriteDao
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(
    private val application: Application,
    private val favoriteDao: FavoriteDao,
    private val cartDao: CartDao
) : BaseViewModel(application) {

    val cartItemCount: LiveData<Int> = liveData {
        cartDao.getCartItemsCount().collect { count ->
            emit(count)
        }
    }

    val favoriteItems: LiveData<List<Product>> = liveData {
        favoriteDao.getFavoriteItems().collect { list ->
            emit(list)
        }
    }

    fun deleteFavoriteItem(item: Product) {
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        viewModelScope.launch(handler) {
            favoriteDao.deleteFavoriteItem(item)
        }
    }
}