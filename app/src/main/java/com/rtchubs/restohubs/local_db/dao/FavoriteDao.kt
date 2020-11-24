package com.rtchubs.restohubs.local_db.dao

import androidx.room.*
import com.rtchubs.restohubs.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItemToFavorite(item: Product): Long

    @Delete
    suspend fun deleteFavoriteItem(item: Product)

    @Query("SELECT * FROM favorite")
    fun getFavoriteItems(): Flow<List<Product>>

    @Query("SELECT COUNT(id) FROM favorite")
    fun getFavoriteItemsCount(): Flow<Int>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE id = :id)")
    fun doesItemExistsInFavorite(id: Int): Flow<Boolean>
}