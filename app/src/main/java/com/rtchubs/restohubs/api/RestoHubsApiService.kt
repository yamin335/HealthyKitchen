package com.rtchubs.restohubs.api

import com.rtchubs.restohubs.models.RProduct
import com.rtchubs.restohubs.models.RProductCategory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RestoHubsApiService {
    @GET(ApiEndPoint.filteredProductCategory)
    suspend fun filteredProductCategory(
        @QueryMap filter: Map<String, String>
    ): Response<List<RProductCategory>>

    @GET(ApiEndPoint.filteredProduct)
    suspend fun filteredProduct(
        @QueryMap filter: Map<String, String>
    ): Response<List<RProduct>>
}