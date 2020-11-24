package com.rtchubs.restohubs.ui.shops

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.rtchubs.restohubs.local_db.dao.CartDao
import com.rtchubs.restohubs.repos.HomeRepository
import com.rtchubs.restohubs.ui.common.BaseViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ShopDetailsViewModel @Inject constructor(
    private val application: Application,
    private val repository: HomeRepository,
    private val cartDao: CartDao
) : BaseViewModel(application) {
    val cartItemCount: LiveData<Int> = liveData {
        cartDao.getCartItemsCount().collect { count ->
            emit(count)
        }
    }
}