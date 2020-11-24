package com.rtchubs.restohubs.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class AllProductResponse(val code: Int?, val status: String?, val message: String?, val data: List<Product>?)

data class ProductCategory(val id: Int?, val name: String?, val description: String?, val merchant_id: Int?, val created_at: String?, val updated_at: String?) : Serializable

@Entity(tableName = "favorite")
data class Product(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String?,
    val barcode: String?,
    val description: String?,
    val buying_price: Double?,
    val selling_price: Double?,
    val mrp: Double?,
    val expired_date: String?,
    val thumbnail: String?,
    val product_image1: String?,
    val product_image2: String?,
    val product_image3: String?,
    val product_image4: String?,
    val product_image5: String?,
    val category_id: Int?,
    val merchant_id: Int?,
    val created_at: String?,
    val updated_at: String?,
    val category: ProductCategory?): Serializable