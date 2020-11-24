package com.rtchubs.restohubs.local_db.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.rtchubs.restohubs.models.Product
import com.rtchubs.restohubs.models.ProductCategory

class RoomDataConverter {
    private val gson by lazy {
        Gson()
    }

    @TypeConverter
    fun jsonStringToCategory(value: String?): ProductCategory? {
        return gson.fromJson(value, ProductCategory::class.java)
    }

    @TypeConverter
    fun categoryToJsonString(category: ProductCategory?): String? {
        return gson.toJson(category)
    }

    @TypeConverter
    fun jsonStringToProduct(value: String): Product {
        return gson.fromJson(value, Product::class.java)
    }

    @TypeConverter
    fun productToJsonString(product: Product): String {
        return gson.toJson(product)
    }

//    @TypeConverter
//    fun fromString(value: String?): ArrayList<String?>? {
//        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
//        return gson.fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayLisr(list: ArrayList<String?>?): String? {
//        return gson.toJson(list)
//    }

}
