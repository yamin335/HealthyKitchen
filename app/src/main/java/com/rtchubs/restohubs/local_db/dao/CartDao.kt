package com.rtchubs.restohubs.local_db.dao

import androidx.room.*
import com.rtchubs.restohubs.local_db.dbo.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItemToCart(item: CartItem): Long

    @Query("UPDATE cart SET quantity = quantity + 1 WHERE id = :id")
    suspend fun increaseProductQuantity(id: Int)

    @Query("UPDATE cart SET quantity = quantity - 1 WHERE id = :id")
    suspend fun decreaseProductQuantity(id: Int)

    @Query("UPDATE cart SET quantity = quantity + :quantity WHERE id = :id")
    suspend fun increaseProductByQuantity(id: Int, quantity: Int)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart")
    fun getCartItems(): Flow<List<CartItem>>

    @Query("SELECT COUNT(id) FROM cart")
    fun getCartItemsCount(): Flow<Int>
}